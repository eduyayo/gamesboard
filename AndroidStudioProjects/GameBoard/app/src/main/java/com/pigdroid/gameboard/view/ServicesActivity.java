package com.pigdroid.gameboard.view;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.gameboard.NotConnectedActivity;
import com.pigdroid.gameboard.app.service.DataService;
import com.pigdroid.gameboard.app.service.PushService;
import com.pigdroid.gameboard.app.service.PushServiceListener;
import com.pigdroid.gameboard.app.service.RestService;
import com.pigdroid.gameboard.app.service.RestServiceListener;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.view.login.LoginActivity;

public class ServicesActivity extends KillableActivity {

    private abstract class PushAbstractListener implements HateAIDLConnection.Listener<PushService>, PushServiceListener {};

    protected PushService pushService;
    protected DataService dataService;
    protected RestService restService;

    private HateAIDLConnection<PushService> pushServiceConnection;
    private HateAIDLConnection<DataService> dataServiceConnection;
    private HateAIDLConnection<RestService> restServiceConnection;

    private boolean paused = true;
    private boolean kicked = false;

    public static abstract class RestListener implements HateAIDLConnection.Listener<RestService>, RestServiceListener {

    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        pushServiceConnection = new HateAIDLConnection<PushService>(this, new PushAbstractListener () {

            @Override
            public void pushServiceFailure() {
                if (PreferenceUtils.getUserEmail(ServicesActivity.this) != null) {
                    // It looks like a real disconnect event!
                    doNotifyNotOnline();
                }
            }

            @Override
            public void pushServiceSuccess() {
                System.out.println("pushServiceSuccess");
            }

            @Override
            public void bound(PushService proxy) {
                pushService = proxy;
                kickOffIf();
            }
        }, PushService.class);

        dataServiceConnection = new HateAIDLConnection<DataService>(this, new HateAIDLConnection.Listener<DataService>() {
            @Override
            public void bound(DataService proxy) {
                dataService = proxy;
                kickOffIf();
            }
        }, DataService.class);

        restServiceConnection = new HateAIDLConnection<RestService>(this, new RestListener() {

            @Override
            public void onRestServiceNotOnline() {
                doNotifyNotOnline();
            }

            @Override
            public void bound(RestService proxy) {
                restService = proxy;
                kickOffIf();
            }

        }, RestService.class);

        if (PreferenceUtils.getUserEmail(this) == null) {
            doLoginUI(false);
        }

    }

    public void doNotifyNotOnline() {
        Thread.dumpStack();
        //This will kill all opened activities for the application.
        restService.doLogoff();
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(new Intent(INTENT_ACTION_KILL));
        Intent intent = new Intent(this, NotConnectedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void doLoginUI(boolean isRetry) {
        //This will kill all opened activities for the application.
//        LocalBroadcastManager.getInstance(this).sendBroadcastSync(new Intent(INTENT_ACTION_KILL));
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isRetry", isRetry);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        pushServiceConnection.disconnect();
        dataServiceConnection.disconnect();
        restServiceConnection.disconnect();
    }

    private void kickOffIf() {
        if (pushService != null
                && dataService != null
                && restService != null
                && !kicked
                && !paused) {
            kicked = true;
            onKickOff();
        }
    }

    protected void onKickOff() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        kicked = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        kickOffIf();
    }


}
