package com.blogspot.mowael.idctask.presenter;

/**
 * Created by moham on 1/4/2017.
 */

public interface FileLoadedListener {
    void getProgress(long progressValue);

    boolean onFileLoaded();
}
