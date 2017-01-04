package com.blogspot.mowael.idctask.presenter;

import android.content.Context;

/**
 * Created by moham on 1/3/2017.
 */

public interface PresenterOperationsToView {

    public Context getAppContext();
    public Context getActivityContext();

    void notifyItemInserted(int layoutPosition);
    void notifyItemRangeChanged(int positionStart, int itemCount);
    void notifyItemRemoved(int position);
    void notifyDataSetChanged();

    void showProgress();
    void hideProgress();

    void requestPermition();



}
