package com.nytreader.alsk.articlesList;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;

import com.nytreader.alsk.R;
import com.nytreader.alsk.utils.ui.recyclerview.LayoutProviderObservableList;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;

import static com.nytreader.alsk.rest.NytArticlesService.HTTP_TOO_MANY_REQUESTS;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

abstract public class ArticlesListViewModel {

    @NonNull
    public final LayoutProviderObservableList articlesList = new LayoutProviderObservableList();
    @NonNull
    public final ObservableField<String> errorMessage = new ObservableField<>();

    @NonNull
    protected final Context context;
    @NonNull
    protected final ArticlesListDataSource dataSource;

    @Nullable
    @VisibleForTesting
    Subscription subscriptionCall;

    ArticlesListViewModel(@NonNull Context context, @NonNull ArticlesListDataSource dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    @CallSuper
    public void tearDown() {
        clearSubscription();
    }

    void setBannerMessage(@StringRes final int resId) {
        setBannerMessage(context.getString(resId));
    }

    @VisibleForTesting
    void setBannerMessage(String message) {
        errorMessage.set(message);
        articlesList.clear();
    }

    @CallSuper
    public void reload() {
        clearSubscription();
        errorMessage.set(null);
        articlesList.clear();
        subscriptionCall = tryLoadNextPage();
    }

    void clearSubscription() {
        if (subscriptionCall != null) {
            subscriptionCall.unsubscribe();
        }
        subscriptionCall = null;
    }

    protected boolean isIdle() {
        return subscriptionCall == null;
    }

    protected abstract Subscription tryLoadNextPage();

    void onError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).code();
            switch (code) {
                case HTTP_TOO_MANY_REQUESTS:
                    setBannerMessage(R.string.error_message_too_many_requests);
                    break;
                case HTTP_FORBIDDEN:
                    setBannerMessage(R.string.error_message_forbidden);
                    break;
                default:
                    setBannerMessage(context.getString(R.string.error_message_generic) + "\n(code: " + code + ")");
                    break;
            }
        } else {
            if (articlesList.isEmpty()) {
                setBannerMessage(R.string.error_message_generic);
            }
        }
    }
}
