package com.blogspot.mowael.idctask.model;

import android.content.IntentFilter;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blogspot.mowael.idctask.presenter.PresenterOperationsToModel;
import com.blogspot.mowael.idctask.utilities.Constants;
import com.blogspot.mowael.idctask.utilities.DownloadResponseListener;
import com.blogspot.mowael.idctask.utilities.InputStreamVolleyRequest;
import com.blogspot.mowael.idctask.utilities.NetworkStateReceiver;
import com.blogspot.mowael.idctask.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by mohamed Wael on 1/3/2017.
 */

public class Model implements PresenterOperationsToModel, NetworkStateReceiver.NetworkStateReceiverListener {


    private final VolleySingleton volley;
    private final RequestQueue queue;
    private final JsonResponseListner responseListner;
    private ArrayList<Item> files;
    private RequiredModelOperations requiredModelOperations;
    private NetworkStateReceiver networkStateReceiver;
    private JsonObjectRequest jsonObjectRequest;

    /**
     * the whole businesscode should lies in the model
     * @param requiredModelOperations the operations that the model needs to perform some issue that comes from the presenter
     */
    public Model(RequiredModelOperations requiredModelOperations) {
        this.requiredModelOperations = requiredModelOperations;
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        volley = VolleySingleton.getInstance(requiredModelOperations.getAppContext());
        queue = volley.getRequestQueue();
        files = new ArrayList<>();
        responseListner = new JsonResponseListner(files);
        createRequest();
    }

    /**
     * a Broadcast receiver for the network.
     * if it's not available it would load the default content in the json file in the assets
     * otherwise it should load content that comes from the server.
     */
    @Override
    public void networkAvailable() {
        requiredModelOperations.getActivityContext().unregisterReceiver(networkStateReceiver);
        createRequest();
        Log.d("requestStatus", "request added to network queue");
    }

    @Override
    public void networkUnavailable() {
        requiredModelOperations.getActivityContext().registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        loadDefaultContent();
    }

    /**
     * returns the item count of the arraylist of items
     * @return
     */
    @Override
    public int getItemCount() {
        return responseListner.getItemArrayList().size();
    }

    @Override
    public boolean isDataLoaded() {
        return responseListner.getItemArrayList().size() > 0;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            queue.stop();
            volley.getDownloadRequestQueue().stop();
            requiredModelOperations = null;
            files.clear();
            responseListner.getItemArrayList().clear();
        }
    }

    @Override
    public boolean downloadPdfFile(Item item, DownloadResponseListener downloadResponseListener) {

        String url = item.getUrl();
        // a link for the head first design pattern book. the idea is to test with a larger size file 28mb
//        String url = Constants.HEAD_URL;
        String encodedUrl = volley.uriEncoder(url);
//        Log.d("url", url);
//        Log.d("encodedUrl", encodedUrl);

        InputStreamVolleyRequest downloadRequest = new InputStreamVolleyRequest(Request.Method.GET, encodedUrl, downloadResponseListener, downloadResponseListener, false, null);

        downloadRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        volley.getDownloadRequestQueue().add(downloadRequest);

        return true;
    }

    private void createRequest() {

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL, new JSONObject(), responseListner, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadDefaultContent();
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);

    }

    private ArrayList<Item> createFileList(JSONArray filesJson, int responseCode, String responseMsg) throws JSONException {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < filesJson.length(); i++) {
            Item item;
            JSONObject file = filesJson.getJSONObject(i);
            if (!file.has("status")) {
                item = new Item(file.getInt("id"), file.getString("name"), file.getString("url"),
                        "", responseCode, responseMsg);
            } else {
                item = new Item(file.getInt("id"), file.getString("name"), file.getString("url"),
                        file.getString("status"), responseCode, responseMsg);
            }
            items.add(item);
        }
        return items;
    }

    public String loadJSONFromAsset() {
        String jsonStr = "";
        try {
            InputStream inputStream = requiredModelOperations.getActivityContext().getAssets().open("default.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonStr = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonStr;
    }

    private void loadDefaultContent() {
        try {
            files = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            String responseMsg = jsonObject.getString("message");
            int responseCode = jsonObject.getInt("code");
            if (responseCode == Constants.DEFAULT_FILES && responseMsg.equals(Constants.RESPONSE_MESSAGE_DEFAULT)) {
                files = createFileList(jsonObject.getJSONArray("files"), responseCode, responseMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class JsonResponseListner implements Response.Listener<JSONObject> {

        ArrayList<Item> itemArrayList;

        public JsonResponseListner(ArrayList<Item> itemArrayList) {
            this.itemArrayList = itemArrayList;
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                Log.d("response", response.toString());
                int responseCode = response.getInt("code");
                String responseMsg = response.getString("message");
                if (responseCode == Constants.RESPONSE_CODE && responseMsg.equals(Constants.RESPONSE_MESSAGE_SUCCESS)) {
                    itemArrayList = createFileList(response.getJSONArray("files"), responseCode, responseMsg);
                    //a listener that takes the arraylist and update the view of the recyclerview
                    requiredModelOperations.onDataLoadedListener(itemArrayList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public ArrayList<Item> getItemArrayList() {
            return itemArrayList;
        }
    }
}
