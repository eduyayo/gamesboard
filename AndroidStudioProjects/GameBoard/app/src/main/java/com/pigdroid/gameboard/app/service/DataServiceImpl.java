package com.pigdroid.gameboard.app.service;

import android.text.TextUtils;

import com.couchbase.lite.Context;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.pigdroid.gameboard.util.JSONUtils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by edu on 09/06/2015.
 */
public class DataServiceImpl extends BaseServiceImpl implements DataService {

    public static final String DATABASE_NAME = "gameboard";
    public static final String DATABASE_VERSION = "0.1";

    private volatile Manager manager;

    private volatile Database database;

    private View contactView;
    private View effectiveContactView;
    private View gameView;
    private View gameByContactCountView;
    private View gameIdsByContactView;
    private View messageView;
    private View messageByGameView;
    private boolean initDone = false;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if (initDone) {
            return;
        }
        Context ctx = new AndroidContext(getApplicationContext());
        try {
            manager = new Manager(ctx, Manager.DEFAULT_OPTIONS);
            database = manager.getDatabase(DATABASE_NAME);
            initDone = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
        createViews();
    }

    private void createViews() {
        contactView = database.getView("contactView");
        contactView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object type = document.get("type");
                if (null != type && "user".equals(type)) {
                    emitter.emit(document.get("email"), document);
                }
            }
        }, DATABASE_VERSION);


        effectiveContactView = database.getView("effectiveContactView");
        effectiveContactView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object type = document.get("type");
                if (null != type && "user".equals(type)) {

                    Boolean accepted = (Boolean) document.get("accepted");
                    Boolean reverseAccepted = (Boolean) document.get("reverseAccepted");

                    accepted = accepted != null ? accepted : false;
                    reverseAccepted = reverseAccepted != null ? reverseAccepted : false;
                    if (accepted && reverseAccepted) {
                        emitter.emit(document.get("email"), document);
                    }
                }
            }
        }, DATABASE_VERSION);


        gameView = database.getView("gameView");
        gameView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object type = document.get("type");
                if (null != type && "game".equals(type)) {
                    emitter.emit(document.get("modelId"), document);
                }
            }
        }, DATABASE_VERSION);

        gameByContactCountView = database.getView("gameByContactCountView");
        gameByContactCountView.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object type = document.get("type");
                if ("game".equals(type)) {
                    if (null != document.get("players")) { //TODO coger el mail del compuesto List<Map>
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> players = (List<Map<String, Object>>) document.get("players");
                        for (Map<String, Object> player : players) {
                            emitter.emit(player.get("email"), document);
                        }
                    }
                }
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
                return keys.size();
            }
        }, DATABASE_VERSION);
        gameIdsByContactView = database.getView("gameIdsByContactView");
        gameIdsByContactView.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object type = document.get("type");
                if ("game".equals(type)) {
                    if (null != document.get("players")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> players = (List<Map<String, Object>>) document.get("players");
                        for (Map<String, Object> player : players) {
                            emitter.emit(player.get("email"), document.get("modelId"));
                        }
                    }
                }
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
                return values;
            }
        }, DATABASE_VERSION);


        messageView = database.getView("messageView");
        messageView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if (null != document.get("payload")) {
                    emitter.emit(document.get("id"), document);
                }
            }
        }, DATABASE_VERSION);

        messageByGameView = database.getView("messageByGameView");
        messageByGameView.setMap/*Reduce*/(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if (null != document.get("payload")) {
                    emitter.emit(document.get("modelId"), document);
                }
            }
        }, DATABASE_VERSION);

    }

    public void deleteData() {
        try {
            for (QueryEnumerator en = database.createAllDocumentsQuery().run(); en.hasNext(); ) {
                QueryRow row = en.next();
                row.getDocument().delete();
            }
            database.compact();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

    public Document storeDocuments(String data) {
        Document ret = null;
        if (!TextUtils.isEmpty(data)) {
            JsonNode node;
            ObjectMapper mapper = JSONUtils.createObjectMapper();
            try {
                node = mapper.readTree(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (node.isArray()) {
                int max = node.size();
                for (int i = 0; i < max; i++) {
                    Map<String, Object> map = mapper.convertValue(node.get(i), new TypeReference<Map<String, Object>>() {});
                    Document doc = database.createDocument();
                    try {
                        doc.putProperties(map);
                    } catch (CouchbaseLiteException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                Map<String, Object> map = mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
                Document doc = database.createDocument();
                try {
                    doc.putProperties(map);
                } catch (CouchbaseLiteException e) {
                    throw new RuntimeException(e);
                }
                ret = doc;
            }
        }
        return ret;
    }

    public Document getDocument(String documentId) {
        return database.getDocument(documentId);
    }

    public Query getCountGameQuery() {
        Query query = gameByContactCountView.createQuery();
        query.setGroupLevel(1);
        return query.toLiveQuery();
    }

    public Query getContactQuery() {
        Query contactQuery = contactView.createQuery();
        contactQuery.setDescending(true);
        return contactQuery;
    }

    public Query getEffectiveContactQuery() {
        Query contactQuery = effectiveContactView.createQuery();
        contactQuery.setDescending(true);
        return contactQuery;
    }

    public int getEffectiveContactCount() {
        try {
            return getEffectiveContactQuery().run().getCount();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

    public Document loadDocument(String id) {
        return database.getDocument(id);
    }

    public Document getContactDocumentByEmail(String email) {
        Query query = contactView.createQuery();
        query.setStartKey(email);
        query.setEndKey(email);
        QueryEnumerator enumerator;
        try {
            enumerator = query.run();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
        Document ret = null;
        if (enumerator.hasNext()) {
            QueryRow row = enumerator.next();
            ret = row.getDocument();
        }
        return ret;
    }

    public List<Document> getMessagesByGame(String modelId) {
        Query query = messageByGameView.createQuery();
        query.setStartKey(modelId);
        query.setEndKey(modelId);
        QueryEnumerator enumerator;
        try {
            enumerator = query.run();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
        List<Document> ret = new ArrayList<>();
        while (enumerator.hasNext()) {
            QueryRow row = enumerator.next();
            ret.add(row.getDocument());
        }
        Collections.sort(ret, new Comparator<Document>() {
            @Override
            public int compare(Document left, Document right) {
                return ((String) left.getProperty("id")).compareTo((String) right.getProperty("id"));
            }
        });
        return ret;
    }

    public Document getGameDocumentByModelId(String id) {
        Query query = gameView.createQuery();
        query.setStartKey(id);
        query.setEndKey(id);
        query.setEndKey(id);
        QueryEnumerator enumerator;
        try {
            enumerator = query.run();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
        Document ret = null;
        if (enumerator.hasNext()) {
            QueryRow row = enumerator.next();
            ret = row.getDocument();
        }
        return ret;
    }


    public Query getGameQuery() {
        Query gameQuery = gameView.createQuery();
        gameQuery.setDescending(true);
        return gameQuery;
    }

    public Query getGamesByContactQuery(String email) {
        final Query gameQueryByContact = gameIdsByContactView.createQuery();
        gameQueryByContact.setStartKey(email);
        gameQueryByContact.setEndKey(email);
        gameQueryByContact.setDescending(true);
        gameQueryByContact.setGroupLevel(1);

        Query gameQuery = gameView.createQuery();
        try {
            List<Object> keys = Collections.emptyList();
            if (gameQueryByContact != null) {
                QueryEnumerator run = gameQueryByContact.run();
                if (run != null) {
                    QueryRow next = run.next();
                    if (next != null) {
                        keys = (List<Object>) next.getValue();
                    }
                }
            }
            gameQuery.setKeys(keys);
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
        return gameQuery;
    }

    public int getContactCount() {
        try {
            return getContactQuery().run().getCount();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isContactAvailable(String id) {
        if (id != null) {
            return isContactAvailable(getDocument(id));
        } else {
            return false;
        }
    }

    public boolean isContactAvailable(Document contact) {
        if (contact != null) {
            return isContactAvailable(contact.getProperties());
        } else {
            return false;
        }
    }

    private boolean isContactAvailable(Map<String, Object> properties) {
        Boolean accepted = (Boolean) properties.get("accepted");
        Boolean iaccepted = (Boolean) properties.get("reverseAccepted");
        if (accepted == null) {
            accepted = false;
        }
        if (iaccepted == null) {
            iaccepted = false;
        }
        return accepted && iaccepted;
    }

}
