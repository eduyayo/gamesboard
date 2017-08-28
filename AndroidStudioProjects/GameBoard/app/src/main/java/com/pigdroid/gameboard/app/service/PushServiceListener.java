package com.pigdroid.gameboard.app.service;

import com.pigdroid.hub.model.message.HubMessage;

public interface PushServiceListener {

    void pushServiceFailure();

    void pushServiceSuccess();

}
