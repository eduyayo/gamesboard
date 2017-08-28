package com.pigdroid.gameboard.app.service;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;

import java.util.List;

public interface DataService extends BaseService {

    Document storeDocuments(String data);

    void deleteData();

    Document getContactDocumentByEmail(String email);

    Document getGameDocumentByModelId(String modelId);

    Document loadDocument(String id);

    public boolean isContactAvailable(String id);

    public boolean isContactAvailable(Document contact);

    Document getDocument(String itemId);

    Query getCountGameQuery();

    Query getContactQuery();

    List<Document> getMessagesByGame(String modelId);

    Query getGameQuery();

    int getContactCount();

    Query getGamesByContactQuery(String email);

    Query getEffectiveContactQuery();

    int getEffectiveContactCount();

}
