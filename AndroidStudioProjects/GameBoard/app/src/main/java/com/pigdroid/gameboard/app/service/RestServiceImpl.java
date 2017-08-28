package com.pigdroid.gameboard.app.service;

import android.os.Build;
import android.util.Base64;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.gameboard.app.RestException;
import com.pigdroid.gameboard.app.TimeoutException;
import com.pigdroid.gameboard.util.DocumentUtils;
import com.pigdroid.gameboard.util.JSONUtils;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.util.crypt.BCryptPasswordEncoder;
import com.pigdroid.hub.model.message.HubMessage;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class RestServiceImpl extends BaseServiceImpl implements RestService {

    private static final int HTTP_CON_TIMEOUT = 30000;

    private static String URL_HTTP = "http";
    private static String URL_HTTPS = "http";
//	private static String URL_REST		= "://192.168.1.75:8080/spring-social-normal-mvc/";

    private static String URL_REST = "://10.0.3.2:8888/spring-social-normal-mvc/";

    private static String URL_BASE = URL_HTTP + URL_REST;
    private static String URL_S_BASE = URL_HTTPS + URL_REST;

    DataService dataService;

    RestServiceListener listener = null;

    HateAIDLConnection<DataService> dataServiceHateAIDLConnection = null;

    static {
        if (!Build.DEVICE.startsWith("vbox")) {
            //PRODUCTION VALUES HERE!
            URL_HTTP = "http";
            URL_HTTPS = "https";
            URL_REST = "://aerogear-pigdroidservices.rhcloud.com/spring-social-normal-mvc/";
            URL_BASE = URL_HTTP + URL_REST;
            URL_S_BASE = URL_HTTPS + URL_REST;
        }

    }

    private Object lock = new Object();
    private volatile String authTokenByDevice;

    private volatile JsonNode user = null;

    private String getAuthTokenByDevice() {
        if (authTokenByDevice == null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
            authTokenByDevice = PreferenceUtils.getUserEmail(this) + ":" + encoder.encode(getDeviceId());
            try {
                authTokenByDevice = Base64.encodeToString(authTokenByDevice.getBytes("UTF-8"), Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return authTokenByDevice;
    }

    private String postForResource(boolean isLoginPost, String baseUrl, String... parameters) throws IOException, RestException, TimeoutException {
        return postForResource(false, isLoginPost, baseUrl, parameters);
    }

    private String postForResource(boolean isJson, boolean isLoginPost, String baseUrl, String... parameters) throws IOException, RestException, TimeoutException {
//		System.out.println("postForResource url("+baseUrl+")");
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CON_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_CON_TIMEOUT);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httpPost = new HttpPost(baseUrl);

        if (parameters != null && parameters.length > 0) {
            if (isJson) {
                StringEntity entity = new StringEntity(parameters[1]);
                entity.setContentType("application/json; charset=utf-8");
                httpPost.setEntity(entity);
            } else {
                int max = parameters.length;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(max / 2);
                for (int i = 0; i < max; i += 2) {
                    nameValuePairs.add(new BasicNameValuePair(parameters[i], parameters[i + 1]));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
        }
        httpPost.setHeader("Accept", "application/json");

        if (isLoginPost) {
            String authToken = PreferenceUtils.getUserEmail(this) + ":" + PreferenceUtils.getUserPassword(this);
            httpPost.setHeader("Authorization", "Basic " + Base64.encodeToString(authToken.getBytes("UTF-8"), Base64.NO_WRAP));
        } else {
            httpPost.setHeader("Authorization", "Basic " + getAuthTokenByDevice());
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
        } catch (SocketTimeoutException e) {
            doNotifyTimeout();
            throw new TimeoutException("Just timed out, try again!");
        } catch (ConnectTimeoutException e) {
            doNotifyTimeout();
            throw new TimeoutException("Just timed out, try again!");
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new RestException("Requires login!");
        }
        StringBuffer buff = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = bufferedReader.readLine();
        while (line != null) {
            buff.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return buff.toString();
    }

    private String getForResource(boolean isLoginPost, String baseUrl, String... parameters)
            throws IOException, RestException, TimeoutException {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CON_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_CON_TIMEOUT);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        int max = parameters.length;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(max / 2);
        for (int i = 0; i < max; i += 2) {
            nameValuePairs.add(new BasicNameValuePair(parameters[i], parameters[i + 1]));
        }

        if (baseUrl.indexOf('?') > -1) {
            baseUrl += "&" + URLEncodedUtils.format(nameValuePairs, "utf-8");
        } else {
            baseUrl += "?" + URLEncodedUtils.format(nameValuePairs, "utf-8");
        }

        HttpGet httpGet = new HttpGet(baseUrl);
        httpGet.setHeader("Accept", "application/json");
        if (isLoginPost) {
            String authToken = PreferenceUtils.getUserEmail(this) + ":" + PreferenceUtils.getUserPassword(this);
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString(authToken.getBytes("UTF-8"), Base64.NO_WRAP));
        } else {
            httpGet.setHeader("Authorization", "Basic " + getAuthTokenByDevice());
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (SocketTimeoutException e) {
            doNotifyTimeout();
            throw new TimeoutException("Just timed out, try again!");
        } catch (ConnectTimeoutException e) {
           doNotifyTimeout();
            throw new TimeoutException("Just timed out, try again!");
        }
        StringBuffer buff = new StringBuffer();
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new RestException("Requires login!");
        }
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = bufferedReader.readLine();
        while (line != null) {
            buff.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return buff.toString();
    }

    public boolean doLogin() {
        boolean ret = false;
        if (PreferenceUtils.getUserEmail(getApplicationContext()) == null) {
            notifyListenersNoLoginConfig();
        } else {
            if (user == null) {
                synchronized (lock) {
                    if (user == null) {
                        try {
                            dataService.deleteData();
                            user = JSONUtils.createObjectMapper().readTree(
                                    postForResource(true,
                                            URL_S_BASE + "/login/user",
                                            "deviceToken", getDeviceId()));
                            fetchData();
                            ret = true;
                        } catch (TimeoutException e) {

                        } catch (IOException e) {
                            ret = false;
                            e.printStackTrace();
//                            if (!fromUI) {
                                notifyListenersNotOnline();
//                            }
                        } catch (RestException e) {
                            ret = false;
//                            if (!fromUI) {
                            notifyListenersLoginFailed(); // Fails because server message
//                                doLoginUI(true);
//                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void notifyListenersNoLoginConfig() {
    }

    private void notifyListenersLoginSuccess() {
    }

    private void notifyListenersLoginFailed() {
    }

    private void notifyListenersNotOnline() {
        doNotifyTimeout();
    }

    private void fetchData() {
        dataService.storeDocuments(fetchContacts());
        dataService.storeDocuments(fetchGames());
    }

    private String fetchGames() {
        synchronized (lock) {
            String ret = null;
            try {
                ret = getForResource(false, URL_BASE + "/user/games");
            } catch (TimeoutException e) {

            } catch (IOException e) {
                notifyListenersNotOnline();
            } catch (RestException e) {
                //TODO doLoginUI(true);
            }
            return ret;
        }
    }

    private String fetchContacts() {
        synchronized (lock) {
            String ret = null;
            try {
                ret = getForResource(false, URL_BASE + "/user/contacts");
            } catch (TimeoutException e) {

            } catch (IOException e) {
                notifyListenersNotOnline();
            } catch (RestException e) {
                //TODO doLoginUI(true);
            }
            return ret;
        }
    }

    public boolean doRegister(String email, String password) {
        boolean ret;
        try {
            JSONUtils.createObjectMapper().readTree(
                    postForResource(false,
                            URL_S_BASE + "/register/user",
                            "email", email,
                            "password", password,
                            "passwordVerification", password));
            ret = true;
        } catch (TimeoutException e) {
            ret = false;
        } catch (IOException e) {
            notifyListenersNotOnline();
            ret = false;
        } catch (RestException e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataServiceHateAIDLConnection = new HateAIDLConnection<DataService>(this, new HateAIDLConnection.Listener<DataService>() {
            @Override
            public void bound(DataService proxy) {
                dataService = proxy;
            }
        }, DataService.class);

        listener = createListenerProxy(RestServiceListener.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataServiceHateAIDLConnection.disconnect();
//        this.dataService.  //Do something?
    }

    public JsonNode getUserProfileJson() {
        if (user == null) {
            synchronized (lock) {
                if (user == null) {
                    doLogin();
                }
            }
        }
        return user;
    }

    @Override
    public void doLogoff() {
        user = null; // So next time we allow this to login
    }

    public JsonNode searchContacts(String email) {
        try {
            return JSONUtils.createObjectMapper().readTree(
                    getForResource(false, URL_BASE + "/contacts", "email", email));
        } catch (TimeoutException e) {

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
//            doNotifyNotOnline();
        } catch (RestException e) {
//            doLogin(false);
            doLogin();
        }
        return null;
    }

    public JsonNode addContact(String email) {
        JsonNode ret = null;
        try {
            ret = JSONUtils.createObjectMapper().readTree(
                    postForResource(false, URL_BASE + "/contact", "email", email));
            if (dataService.getContactDocumentByEmail(email) == null) {
                dataService.storeDocuments(ret.toString());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {

        } catch (IOException e) {
//            doNotifyNotOnline();
        } catch (RestException e) {
//            doLogin(false);
            doLogin();
        }
        return ret;
    }

    public Document addGame(String serializedModel, String gameName) {
        Document ret = null;
        try {
            HubMessage message = new HubMessage(HubMessage.TYPE_ADD_GAME);
            message.setId(gameName);
            message.setPayload(serializedModel);
            message.setFrom(PreferenceUtils.getUserEmail(this));
            ret = dataService.storeDocuments(
                    postForResource(true, false, URL_BASE + "/game", "mesage",
                            JSONUtils.createObjectMapper().writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {

        } catch (IOException e) {
//            doNotifyNotOnline();
        } catch (RestException e) {
//            doLogin(false);
            doLogin();
        }
        return ret;
    }

    public String sendMessage(HubMessage message) {
        String ret = null;
        try {
            ObjectMapper mapper = JSONUtils.createObjectMapper();
            ret = postForResource(
                    true,
                    false,
                    URL_BASE + "/message",
                    "message",
                    mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {

        } catch (IOException e) {
//            doNotifyNotOnline();
        } catch (RestException e) {
//            doLogin(false);
            doLogin();
        }
        return ret;
    }

    public Document joinGame(String id) {
        Document document = dataService.loadDocument(id); // document.getProperty("modelId");
        Document ret = null;
        try {
            ret = dataService.storeDocuments(postForResource(true, false, URL_BASE + String.format("/game/%s/join", document.getProperty("modelId"))));
            document.delete();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {

        } catch (IOException e) {
//            doNotifyNotOnline();
        } catch (RestException e) {
//            doLogin(false);
            doLogin();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return document;
    }

//    public Document joinGame(String id) {
//        Document document = dataService.loadDocument(id); // document.getProperty("modelId");
//        Document ret = null;
//        try {
//            ret = dataService.storeDocuments(postForResource(true, false, URL_BASE + String.format("/game/%s/join", document.getProperty("modelId"))));
//            Map<String, Object> properties = new HashMap<String, Object>(document.getProperties());
//            for (Map.Entry<String, Object> entry : ret.getProperties().entrySet()) {
//                if (!"id".equals(entry.getKey()) && !"_id".equals(entry.getKey())) {
//                    properties.put(entry.getKey(), entry.getValue());
//                }
//            }
//
//            UnsavedRevision revision = document.createRevision();
//            revision.setProperties(properties);
//            ret.delete();
//            revision.save();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } catch (TimeoutException e) {
//
//        } catch (IOException e) {
////            doNotifyNotOnline();
//        } catch (RestException e) {
////            doLogin(false);
//            doLogin();
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//            throw new RuntimeException();
//        }
//        return document;
//    }

    @Override
    public void leaveGame(String id) {
        Document document = dataService.loadDocument(id); // document.getProperty("modelId");
        Document ret = null;
        try {
            postForResource(true, false, URL_BASE + String.format("/game/%s/leave", document.getProperty("modelId")));
            DocumentUtils.setProperty(document, "gameEstate", "INVALID");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {

        } catch (IOException e) {
//            doNotifyNotOnline();
        } catch (RestException e) {
//            doLogin(false);
            doLogin();
        }
    }

    private void doNotifyTimeout() {
        listener.onRestServiceNotOnline();
    }

}