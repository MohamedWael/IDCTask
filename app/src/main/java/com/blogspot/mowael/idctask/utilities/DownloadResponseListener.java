package com.blogspot.mowael.idctask.utilities;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.blogspot.mowael.idctask.presenter.FileLoadedListener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by moham on 1/4/2017.
 */

public class DownloadResponseListener implements Response.Listener<byte[]>, Response.ErrorListener {
    private Request request;
    private String fileName;
    private File file, path;
    private String filePath;
    private FileLoadedListener fileLoadedListener;
    private long lenghtOfFile;
    private DownloadedListener downloadedListener;

    public DownloadResponseListener(FileLoadedListener fileLoadedListener, String fileName) {
        this.fileName = fileName;
        this.fileLoadedListener = fileLoadedListener;
        //            File path = Environment.getDataDirectory();
//            File path = mContext.getFilesDir();
        path = Environment.getExternalStorageDirectory();
        file = new File(path, fileName + ".pdf");
        filePath = file.toString();
        Log.d("filePath", file.toString());
        downloadedListener = new DownloadedListener() {

            @Override
            public void isDownloaded() {

                DownloadResponseListener.this.fileLoadedListener.onFileLoaded();
            }
        };
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public void onResponse(final byte[] response) {
        lenghtOfFile = response.length;
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.d("startDownload", lenghtOfFile + "");
                    //covert reponse to input stream
                    InputStream input = new ByteArrayInputStream(response);
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                    byte data[] = new byte[1024];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                        fileLoadedListener.getProgress((int) total * 100 / lenghtOfFile);
                    }
                    output.flush();
                    output.close();
                    input.close();


                    downloadedListener.isDownloaded();

                    Log.d("endOfDownload", lenghtOfFile + "");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    private interface DownloadedListener {
        void isDownloaded();
    }
}