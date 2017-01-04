package com.blogspot.mowael.idctask.presenter;

import com.blogspot.mowael.idctask.model.Item;
import com.blogspot.mowael.idctask.utilities.DownloadResponseListener;

/**
 * Created by moham on 1/3/2017.
 */

public interface PresenterOperationsToModel {
    int getItemCount();

    boolean isDataLoaded();

    void onDestroy(boolean isChangingConfiguration);

    boolean downloadPdfFile(Item item, DownloadResponseListener downloadResponseListener);
}
