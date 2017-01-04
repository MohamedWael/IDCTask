package com.blogspot.mowael.idctask.view;

import android.view.View;
import android.view.ViewGroup;

import com.blogspot.mowael.idctask.model.FileRowViewHolder;

/**
 * Created by moham on 1/3/2017.
 */

public interface ViewOperationsToPresenter {

    void onViewClickEvent(View view);

    FileRowViewHolder createViewHolder(ViewGroup parent, int viewType);

    void bindViewHolder(FileRowViewHolder holder, int position);

    int getItemCount();

    void onDestroy(boolean isChangingConfiguration);

}
