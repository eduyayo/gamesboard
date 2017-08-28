package com.pigdroid.gameboard.util;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by edu on 29/07/2015.
 */
public class DocumentUtils {

    private DocumentUtils() {

    }

    public static void setProperty(final Document document, final String property, final Object value) {
        Map<String, Object> properties = new HashMap<String, Object>(document.getProperties());
        properties.put(property, value);
        UnsavedRevision revision = document.createRevision();
        revision.setProperties(properties);
        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setProperties(final Document document, final Map<String, Object> newProperties) {
        Map<String, Object> properties = new HashMap<String, Object>(document.getProperties());
        properties.putAll(newProperties);
        UnsavedRevision revision = document.createRevision();
        revision.setProperties(properties);
        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

}
