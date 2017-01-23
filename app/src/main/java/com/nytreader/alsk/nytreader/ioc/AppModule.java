package com.nytreader.alsk.nytreader.ioc;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nytreader.alsk.nytreader.BuildConfig;
import com.nytreader.alsk.nytreader.R;
import com.nytreader.alsk.nytreader.rest.NytArticlesService;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@Module
public class AppModule {

    public static final String END_POINT = "END_POINT";
    public static final String END_POINT_IMAGES = "END_POINT_IMAGES";
    public static final String THUMB_SIZE = "THUMB_SIZE";

    private final Context context;

    public AppModule(final Application application) {
        context = application;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Named(AppModule.END_POINT)
    @NonNull
    @Provides
    public String provideEndPoint() {
        return BuildConfig.NYT_END_POINT;
    }

    @Named(AppModule.END_POINT_IMAGES)
    @NonNull
    @Provides
    public String provideEndPointImages() {
        return BuildConfig.NYT_END_POINT_IMAGES;
    }

    @Provides
    @Singleton
    @Named(THUMB_SIZE)
    public int provideThumbSize(@NonNull Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.thumb_size);
    }

    @NonNull
    @Provides
    @Singleton
    public NytArticlesService providesNytArticlesServices(@NonNull @Named(END_POINT) String endPoint) {

        // tune timeouts
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);

        // create Retrofit object for farther services initialization
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(endPoint)
                .client(okHttpClient)
                .build();

        // create flickr service
        return retrofit.create(NytArticlesService.class);
    }
}
