package com.blogspot.mowael.idctask.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.blogspot.mowael.idctask.model.FileRowViewHolder;

/**
 * Created by moham on 1/3/2017.
 */

public class FileRVAdapter extends RecyclerView.Adapter<FileRowViewHolder> {

    private Context mContext;
    private ViewOperationsToPresenter viewOperationsToPresenter;

    public FileRVAdapter(Context mContext, ViewOperationsToPresenter viewOperationsToPresenter) {
        this.mContext = mContext;
        this.viewOperationsToPresenter = viewOperationsToPresenter;
    }

    @Override
    public FileRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewOperationsToPresenter.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(FileRowViewHolder holder, int position) {
        viewOperationsToPresenter.bindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return viewOperationsToPresenter.getItemCount();
    }
}
