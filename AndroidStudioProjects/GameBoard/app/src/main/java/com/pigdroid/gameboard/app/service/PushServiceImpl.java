package com.pigdroid.gameboard.app.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.LruCache;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.gameboard.DisconnectedActivity;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.JSONUtils;
import com.pigdroid.gameboard.util.NotificationUtils;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.view.detail.contact.ContactDetailActivity;
import com.pigdroid.gameboard.view.detail.contact.ContactListActivity;
import com.pigdroid.gameboard.view.detail.game.GameDetailActivity;
import com.pigdroid.gameboard.view.detail.game.GameListActivity;
import com.pigdroid.hub.model.message.HubMessage;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PushServiceImpl extends BaseServiceImpl implements PushService {

    // Config variables
    private static final String AG_SECRET		= "010ceacc-7e77-4a7d-a26e-404784e8d1ad";
    private static final String AG_VARIANTID	= "4ae83272-5db7-41f6-879d-c907a39bd3fc";
    private static final String AG_SENDERID		= "256699055978";
    private static final String URL_UPUSH		= "https://aerogear-pigdroidservices.rhcloud.com/ag-push/";
	
	private volatile PushConfig pushConfig;
	private volatile Registrations registrations;
	private volatile MessageHandler messageHandler;
	private volatile PushRegistrar registrar;

    private DataService dataService = null;
    private HateAIDLConnection<DataService> dataServiceConnection = null;
    private RestService restService = null;
    private HateAIDLConnection<RestService> restServiceConnection = null;

    private boolean failure = false;

    private LruCache messageIds = new LruCache(20);

    PushServiceListener listener = createListenerProxy(PushServiceListener.class);

    public void onCreate() {
        dataServiceConnection = new HateAIDLConnection<DataService>(this, new HateAIDLConnection.Listener<DataService>() {
            @Override
            public void bound(DataService proxy) {
                dataService = proxy;
            }
        }, DataService.class);

        restServiceConnection = new HateAIDLConnection<RestService>(this, new HateAIDLConnection.Listener<RestService>() {
            @Override
            public void bound(RestService proxy) {
                restService = proxy;
            }
        }, RestService.class);
    }

    public void onDestroy() {
        dataServiceConnection.disconnect();
        restServiceConnection.disconnect();
    }

    private void initRegistrars() {
        GameBoardApplication application = (GameBoardApplication) getApplication();
        if (pushConfig == null) {
            failure = false;
            if (PreferenceUtils.getUserEmail(this) != null) {
                try {
                    pushConfig = new PushConfig(new URI(URL_UPUSH), AG_SENDERID);
                    pushConfig.setAlias(getDeviceId());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                pushConfig.setVariantID(AG_VARIANTID);
                pushConfig.setSecret(AG_SECRET);
                registrations = new Registrations();
                registrar = registrations.push("registrar", pushConfig);
                registrar.register(getApplicationContext(), new Callback<Void>() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void onSuccess(Void ignore) {
                        doNotifyListenersSuccess();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        exception.printStackTrace();
                        doCleanRegistrars();
                        doNotifyListenersFailure();
                    }
                });
                Registrations.registerMainThreadHandler(messageHandler = new MessageHandler() {

                    @Override
                    public void onMessage(android.content.Context ctx, Bundle msg) {
                        synchronized (messageIds) {
                            String payload = msg.getString("payload");
                            Integer hash = payload.hashCode();
                            String hashString = hash.toString();
                            if (messageIds.get(hashString) == null) {
                                messageIds.put(hashString, hash);
                                ObjectMapper mapper = JSONUtils.createObjectMapper();
                                HubMessage message = null;
                                try {
                                    message = mapper.convertValue(mapper.readTree(payload), HubMessage.class);
                                } catch (IllegalArgumentException e) {
                                    throw new RuntimeException(e);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                processMessage(message);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        doCleanRegistrars();
                        doNotifyListenersFailure();
                    }

                    @Override
                    public void onDeleteMessage(android.content.Context ctx, Bundle msg) {
                        System.out.println("deleted " + msg);
                    }

                });
            } else {
                doNotifyListenersFailure();
            }
        } else {
            doNotifyListenersSuccess(); //...So new connections know we are already on.
        }
    }

//    @Override
//	public void onDestroy() {
//		Registrations.unregisterMainThreadHandler(messageHandler);
//		registrar.unregister(getApplicationContext(), null);
//		super.onDestroy();
//	}

//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}


    @Override
    public IBinder onBind(Intent intent) {
        initRegistrars();
        return super.onBind(intent);
    }

    private void doNotifyListenersSuccess() {
//        super.notifyListeners(notifierSuccess);
        listener.pushServiceSuccess();
    }

    private void doNotifyListenersFailure() {
//        super.notifyListeners(notifierFailure);
        listener.pushServiceFailure();
    }

    public void processMessage(HubMessage message) {
        switch (message.getType()) {
            case HubMessage.TYPE_ADD_CONTACT:
                Intent resultIntent = new Intent(this, ContactListActivity.class);
                Document contact = null;
                contact = dataService.getContactDocumentByEmail(message.getFrom());
                if (contact == null) {
                    contact = dataService.storeDocuments(message.getPayload()); //Store the guy who just added us!
                    resultIntent.putExtra("id", contact.getId());
                }
//			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                NotificationUtils.createNotification(
                        this,
                        contact.getId(),
                        resultIntent,
                        "A buddy has added you to the contact list!",
                        "%s buddie has added you to the contact list!",
                        contact.getProperties().get("email"));
                break;
            case HubMessage.TYPE_ADD_GAME:
                Document game = dataService.storeDocuments(message.getPayload());

                resultIntent = new Intent(this, GameListActivity.class);
                contact = dataService.getContactDocumentByEmail(message.getFrom());
                resultIntent.putExtra("id", game.getId());
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NotificationUtils.createNotification(
                        this,
                        game.getId(),
                        resultIntent,
                        "You've been invited to a new game!",
                        "Join or leave the game you've been added to...");//TODO Add the guy who invites
                break;
            case HubMessage.TYPE_MSG_CHAT:
                Intent intent = new Intent(GameBoardApplication.INTENT_ACTION_CHAT);
                intent.putExtra(Intent.EXTRA_EMAIL, message.getFrom());
                intent.putExtra(Intent.EXTRA_TEXT, message.getPayload());
                LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
                if (intent.hasExtra(Intent.EXTRA_DATA_REMOVED)) {
                    //It was handled
                } else {
                    Document document = dataService.getContactDocumentByEmail(message.getFrom());
                    intent = new Intent(this, ContactDetailActivity.class);
                    intent.putExtra("id", document.getId());
                    intent.putExtra(Intent.EXTRA_EMAIL, message.getFrom());
                    intent.putExtra(Intent.EXTRA_TEXT, message.getPayload());
                    NotificationUtils.createNotification(
                            this,
                            document.getId(),
                            intent,
                            "New message!",
                            "%s just sent you a new message!",
                            document.getProperties().get("email"));
                }
                break;
            case HubMessage.TYPE_MSG_GAME:
                processGameMessage(message);
                break;
            case HubMessage.TYPE_MSG_DISCONNECT:
                restService.doLogoff();
                intent = new Intent(this, DisconnectedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    /**
     * In here, just raise the intent and, when not handled,
     * save it for later.
     *
     */
    private void processGameMessage(HubMessage message) {
        Document gameMessageDocument = dataService.storeDocuments(message.getPayload());
        Intent intent = new Intent(GameBoardApplication.INTENT_ACTION_GAME_MSG);
        intent.putExtra("id", gameMessageDocument.getId());
        intent.putExtra("modelId", gameMessageDocument.getProperty("modelId").toString());

        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
        if (intent.hasExtra(Intent.EXTRA_DATA_REMOVED)) {
            // Handled already!!
            try {
                gameMessageDocument.delete();
            } catch (CouchbaseLiteException e) {
                throw new RuntimeException();
            }
        } else {
            Document contactDocument = dataService.getContactDocumentByEmail(message.getFrom());
            intent = new Intent(this, GameDetailActivity.class);
            intent.putExtra("id", dataService.getGameDocumentByModelId(gameMessageDocument.getProperty("modelId").toString()).getId());
            NotificationUtils.createNotification(
                    this,
                    contactDocument.getId(),
                    intent,
                    "New game movement!",
                    "%s just made a movement.",
                    contactDocument.getProperties().get("email"));
        }
    }

    void doCleanRegistrars() {
        pushConfig = null;
        registrations = null;
        messageHandler = null;
//        registrar.unregister(getApplicationContext(), null);
//        registrar = null;
        if (messageHandler != null) {
            Registrations.unregisterMainThreadHandler(messageHandler);
        }
        failure = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initRegistrars();
        return super.onStartCommand(intent, flags, startId);
    }

}