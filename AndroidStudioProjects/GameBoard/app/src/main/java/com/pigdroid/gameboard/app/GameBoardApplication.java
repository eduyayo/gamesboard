package com.pigdroid.gameboard.app;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.multidex.MultiDexApplication;

import com.couchbase.lite.Document;
import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.gameboard.TimeoutActivity;
import com.pigdroid.gameboard.app.service.DataService;
import com.pigdroid.gameboard.app.service.DataServiceImpl;
import com.pigdroid.gameboard.app.service.PushService;
import com.pigdroid.gameboard.app.service.PushServiceImpl;
import com.pigdroid.gameboard.app.service.RestService;
import com.pigdroid.gameboard.app.service.RestServiceImpl;
import com.pigdroid.gameboard.view.detail.game.tile.ImageUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.io.InputStream;

public class GameBoardApplication extends MultiDexApplication {

	public static final String INTENT_ACTION_CHAT = "com.pigdroid.gameboard.CHAT";
	public static final String INTENT_ACTION_GAME_MSG = "com.pigdroid.gameboard.GAME_MSG";

	private PushService pushService;
	private DataService dataService;
	private RestService restService;

    private HateAIDLConnection<PushService> pushServiceConnection;
    private HateAIDLConnection<DataService> dataServiceConnection;
    private HateAIDLConnection<RestService> restServiceConnection;

    private Object lock = new Object();
	
	private volatile String authTokenByDevice;
	
	private static Picasso picasso = null;
	
	public static Picasso getPicasso() {
		return picasso;
	}
	
	public void initServerRegisters() {
//		Intent startServiceIntent = new Intent(this, PushServiceImpl.class);
//		this.startService(startServiceIntent);

	}

	@Override
	public void onCreate() {
		super.onCreate();

		Intent service = new Intent(this, DataServiceImpl.class);
		this.startService(service);

		service = new Intent(this, RestServiceImpl.class);
        this.startService(service);

		service = new Intent(this, PushServiceImpl.class);
		this.startService(service);



		picasso = new Picasso.Builder(this).addRequestHandler(new RequestHandler() {

			@Override
			public boolean canHandleRequest(Request request) {
				if (request.uri != null && "svg".equals(request.uri.getScheme())) {
					return true;
				}
				return false;
			}

			@Override
			public Result load(Request request, int rq) throws IOException {
				InputStream inputStream = GameBoardApplication.class.getClassLoader().getResourceAsStream(request.uri.toString().substring("svg://".length()));
				return new Result(ImageUtils.createBitmap(inputStream, request.targetWidth, request.targetHeight, true), LoadedFrom.MEMORY);
			}
			
		}).addRequestHandler(new RequestHandler() {

			@Override
			public boolean canHandleRequest(Request request) {
				if (request.uri != null && "screenShot".equals(request.uri.getScheme())) {
					return true;
				}
				return false;
			}

			@Override
			public Result load(Request request, int rq) throws IOException {
				String id = request.uri.toString().substring("screenShot://".length());
                Document document = dataService.getDocument(id);
                byte[] array = (byte[]) document.getProperty("screenShot");
                if (array != null) {
                    return new Result(BitmapFactory.decodeByteArray(array, 0, array.length), LoadedFrom.MEMORY);
                } else {
                    return null;
                }
			}

		}).build();

        pushServiceConnection = new HateAIDLConnection<PushService>(this, new HateAIDLConnection.Listener<PushService>() {
            @Override
            public void bound(PushService proxy) {
                pushService = proxy;
            }
        }, PushService.class);
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

        ///login was here

	}

	private void doNotifyTimeout() {
		Thread.dumpStack();
		Intent intent = new Intent(this, TimeoutActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}