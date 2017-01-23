package com.nytreader.alsk.nytreader;

import android.app.Application;

import com.nytreader.alsk.nytreader.ioc.IoC;

public class NytReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IoC.getInstance().initDependencyGraph(this);
    }
}
