package com.nytreader.alsk.ioc;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.nytreader.alsk.BuildConfig;
import com.nytreader.alsk.R;
import com.nytreader.alsk.rest.NytArticlesService;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    @Provides
    public Handler provideHandler() {
        return new Handler();
    }

    @NonNull
    @Provides
    @Singleton
    public NytArticlesService providesNytArticlesServices(@NonNull @Named(END_POINT) String endPoint) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // add logger for Debug mode only
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logger);
        }

        final OkHttpClient okHttpClient = builder
                // tune timeouts
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(endPoint)
                .client(okHttpClient)
                .build();

        // create flickr service
        return retrofit.create(NytArticlesService.class);
    }
}
