package com.blogspot.mowael.idctask.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blogspot.mowael.idctask.R;
import com.blogspot.mowael.idctask.model.FileRowViewHolder;
import com.blogspot.mowael.idctask.model.Item;
import com.blogspot.mowael.idctask.model.RequiredModelOperations;
import com.blogspot.mowael.idctask.utilities.DownloadResponseListener;
import com.blogspot.mowael.idctask.view.ViewOperationsToPresenter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Created by moham on 1/3/2017.
 */

public class Presenter implements RequiredModelOperations, ViewOperationsToPresenter {

    private PresenterOperationsToModel mModel;
    private WeakReference<PresenterOperationsToView> weakReferenceView;
    //    private PresenterOperationsToView view;
    private ArrayList<Item> files;

    public Presenter(PresenterOperationsToView presenterOperationsToView) {
//        this.view = presenterOperationsToView;
        this.weakReferenceView = new WeakReference<>(presenterOperationsToView);
        files = new ArrayList<>();
    }

    PresenterOperationsToView getView() {
        return weakReferenceView.get();
    }

    @Override
    public Context getAppContext() {

        return getView().getAppContext();
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public void onDataLoadedListener(ArrayList<Item> items) {
        files = items;
        loadData();
    }

    /**
     * the click listener of the main activity
     * @param view
     */
    @Override
    public void onViewClickEvent(View view) {

    }

    @Override
    public FileRowViewHolder createViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_content_row, parent, false);
        return new FileRowViewHolder(itemView);
    }

    @Override
    public void bindViewHolder(final FileRowViewHolder holder, int position) {
//        Item item = mModel.getFiles().get(position);
        final Item item = files.get(position);
        holder.tvFileName.setText(item.getFileName());
        holder.tvStatus.setVisibility(View.GONE);

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                int writePermissionCheck = ContextCompat.checkSelfPermission(getActivityContext(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                int readPermissionCheck = ContextCompat.checkSelfPermission(getActivityContext(),
//                        Manifest.permission.READ_EXTERNAL_STORAGE);
//
//                if (Build.VERSION.SDK_INT < 19) {
//                    getView().requestPermition();
//                }
                // the response and the error listener of the download request added to them a listener
                // which listens for the progress upgrading and whether the download completed
                DownloadResponseListener downloadResponseListener = new DownloadResponseListener(new FileLoadedListener() {
                    @Override
                    public void getProgress(long progressValue) {
                        Log.d("progress", progressValue + "");
//                        holder.btnDownload.setText(progressValue + "%");
                        holder.progressBar.setProgress((int) progressValue);
                    }

                    @Override
                    public boolean onFileLoaded() {
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText("Downloaded");
//                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnDownload.setText("open");
                        return true;
                    }

                }, item.getFileName());
                if (holder.btnDownload.getText().toString().equals("open")) {
                    displayPDF(item.getFileName());
                } else {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(0);
                    mModel.downloadPdfFile(item, downloadResponseListener);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModel.getItemCount();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mModel.onDestroy(isChangingConfiguration);
    }

    public void setModel(PresenterOperationsToModel model) {
        this.mModel = model;
    }

    private void loadData() {
        try {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    //checking weather data Loaded in Model
                    return mModel.isDataLoaded();
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    try {
                        if (!result) // Loading error
                            toastMsg("error loading data");
                        else { // success
                            getView().notifyDataSetChanged();
                            getView().hideProgress();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void toastMsg(String msg) {
        Toast.makeText(getView().getActivityContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void displayPDF(String fileName) {

        File file = null;

        Log.d("file_path", Environment.getExternalStorageDirectory() + fileName + ".pdf");
        Log.d("file_abs_path", getAppContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath());
        file = new File(getAppContext().getFilesDir() + fileName + ".pdf");
        if (file.exists()) {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                getActivityContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                // Instruct the user to install a PDF reader here, or something
            }
        } else
            Toast.makeText(getActivityContext(), "File path is incorrect.", Toast.LENGTH_LONG).show();
    }


}
