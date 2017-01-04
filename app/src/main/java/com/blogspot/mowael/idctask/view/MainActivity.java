package com.blogspot.mowael.idctask.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.blogspot.mowael.idctask.R;
import com.blogspot.mowael.idctask.model.Model;
import com.blogspot.mowael.idctask.presenter.Presenter;
import com.blogspot.mowael.idctask.presenter.PresenterOperationsToView;
import com.blogspot.mowael.idctask.utilities.Constants;

public class MainActivity extends AppCompatActivity implements PresenterOperationsToView, View.OnClickListener {

    private ViewOperationsToPresenter viewOperationsToPresenter;
    private RecyclerView rvMainContent;
    private FileRVAdapter fileRVAdapter;
    private ProgressBar pbRecyclerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions((MainActivity) getActivityContext(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                Constants.WRITE_READ_EXTERNAL_PERMISSIONS);
        setUpMVP();
        pbRecyclerData = (ProgressBar) findViewById(R.id.pbRecyclerData);
        rvMainContent = (RecyclerView) findViewById(R.id.rvMainContent);
        rvMainContent.setLayoutManager(new LinearLayoutManager(this));
        fileRVAdapter = new FileRVAdapter(this, viewOperationsToPresenter);
        rvMainContent.setAdapter(fileRVAdapter);
        rvMainContent.setKeepScreenOn(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.WRITE_READ_EXTERNAL_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    //Call whatever you want
                    Log.d("permition", "permition granter");
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewOperationsToPresenter.onDestroy(isChangingConfigurations());
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void notifyItemInserted(int layoutPosition) {
        fileRVAdapter.notifyItemInserted(layoutPosition);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        fileRVAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void notifyItemRemoved(int position) {
        fileRVAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyDataSetChanged() {
        fileRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        pbRecyclerData.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbRecyclerData.setVisibility(View.GONE);
    }

    @Override
    public void requestPermition() {
        Snackbar.make(findViewById(android.R.id.content), "Please Grant Permissions", Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.WRITE_READ_EXTERNAL_PERMISSIONS);
                    }
                }).show();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.WRITE_READ_EXTERNAL_PERMISSIONS);
            }
        } else {
            //Call whatever you want
            Log.d("permition", "Call whatever you want");
        }
    }


    @Override
    public void onClick(View v) {
        viewOperationsToPresenter.onViewClickEvent(v);
    }

    /**
     *
     */
    private void setUpMVP() {
        Presenter mPresenter = new Presenter(this);
        Model mModel = new Model(mPresenter);
        mPresenter.setModel(mModel);
        viewOperationsToPresenter = mPresenter;
    }
}
