package com.blogspot.mowael.idctask.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogspot.mowael.idctask.R;

/**
 * Created by moham on 1/3/2017.
 */

public class FileRowViewHolder extends RecyclerView.ViewHolder {
    public TextView tvFileName, tvStatus;
    public Button btnDownload;
    public ProgressBar progressBar;

    /***
     * initializing the row view of the Recyclerviewer
     * @param itemView the row view in the main_content_row.xml
     */
    public FileRowViewHolder(View itemView) {
        super(itemView);
        tvFileName = (TextView) itemView.findViewById(R.id.tvFileName);
        tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
        btnDownload = (Button) itemView.findViewById(R.id.btnDownload);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
    }
}
