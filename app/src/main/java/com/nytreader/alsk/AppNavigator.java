package com.nytreader.alsk;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppNavigator {

    @NonNull
    private final Context context;

    @Inject
    public AppNavigator(@NonNull Context context) {
        this.context = context;
    }

    public void startSearchActivity() {
        context.startActivity(new Intent(context, SearchActivity.class));
    }
}
