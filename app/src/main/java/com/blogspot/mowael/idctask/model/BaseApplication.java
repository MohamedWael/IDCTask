package com.blogspot.mowael.idctask.model;

import android.app.Application;

import com.blogspot.mowael.idctask.utilities.VolleySingleton;

/**
 * Created by moham on 1/3/2017.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //starting network queue
        VolleySingleton volleySingleton = VolleySingleton.getInstance(this);
        volleySingleton.getRequestQueue().start();
        volleySingleton.getDownloadRequestQueue().start();
    }
}
