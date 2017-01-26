package com.nytreader.alsk;

import android.app.Application;

import com.nytreader.alsk.ioc.IoC;

public class NytReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IoC.getInstance().initDependencyGraph(this);
    }
}
