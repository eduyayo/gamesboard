package com.pigdroid.gameboard.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ListFragment;

import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.gameboard.app.service.DataService;
import com.pigdroid.gameboard.app.service.DataServiceImpl;
import com.pigdroid.gameboard.app.service.PushService;
import com.pigdroid.gameboard.app.service.PushServiceImpl;
import com.pigdroid.gameboard.app.service.RestService;
import com.pigdroid.gameboard.app.service.RestServiceImpl;
import com.pigdroid.hub.model.message.HubMessage;

public class ServiceListFragment extends ListFragment {

    protected PushService pushService;
    protected DataService dataService;
    protected RestService restService;

    private HateAIDLConnection<PushService> pushServiceConnection;
    private HateAIDLConnection<DataService> dataServiceConnection;
    private HateAIDLConnection<RestService> restServiceConnection;


        @Override
    public void onAttach(Activity activity) {
            super.onAttach(activity);

            pushServiceConnection = new HateAIDLConnection<PushService>(activity, new HateAIDLConnection.Listener<PushService>() {
                @Override
                public void bound(PushService proxy) {
                    pushService = proxy;
                    kickOffIf();
                }
            }, PushService.class);
            dataServiceConnection = new HateAIDLConnection<DataService>(activity, new HateAIDLConnection.Listener<DataService>() {
                @Override
                public void bound(DataService proxy) {
                    dataService = proxy;
                    kickOffIf();
                }
            }, DataService.class);
            restServiceConnection = new HateAIDLConnection<RestService>(activity, new HateAIDLConnection.Listener<RestService>() {
                @Override
                public void bound(RestService proxy) {
                    restService = proxy;
                    kickOffIf();
                }
            }, RestService.class);


        }

    @Override
    public void onDetach() {
        super.onDetach();
        pushServiceConnection.disconnect();
        dataServiceConnection.disconnect();
        restServiceConnection.disconnect();
    }

    private void kickOffIf() {
        if (pushService != null
                && dataService != null
                && restService != null) {
            onKickOff();
        }
    }
    protected void onKickOff() {
    }

}
