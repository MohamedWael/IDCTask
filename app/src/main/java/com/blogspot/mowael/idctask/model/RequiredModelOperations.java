package com.blogspot.mowael.idctask.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by moham on 1/3/2017.
 */

public interface RequiredModelOperations {
    Context getAppContext();

    Context getActivityContext();

    void onDataLoadedListener(ArrayList<Item> items);
}
