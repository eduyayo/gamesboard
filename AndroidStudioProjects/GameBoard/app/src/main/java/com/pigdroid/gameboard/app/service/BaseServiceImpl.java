package com.pigdroid.gameboard.app.service;

import android.provider.Settings;

import com.pigdroid.android.hateaidl.HateAIDLService;
import com.pigdroid.gameboard.util.PreferenceUtils;

public abstract class BaseServiceImpl extends HateAIDLService implements BaseService {

    protected boolean isUserConfigured() {
        return PreferenceUtils.getUserPassword(this) == null;
    }

    protected String getDeviceId() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID) + "000" + PreferenceUtils.getUserEmail(this);
    }

}
