package com.pigdroid.gameboard.app.service;

import com.couchbase.lite.Document;
import com.pigdroid.hub.model.message.HubMessage;

import org.codehaus.jackson.JsonNode;

public interface RestService extends BaseService {

    public boolean doLogin();

    Document addGame(String serializedModel, String gameName);

    boolean doRegister(String email, String password);

    JsonNode searchContacts(String searchTerm);

    public Document joinGame(String id);

    JsonNode addContact(String email);

    String sendMessage(HubMessage message);

    JsonNode getUserProfileJson();

    void doLogoff();

    void leaveGame(String id);
}
