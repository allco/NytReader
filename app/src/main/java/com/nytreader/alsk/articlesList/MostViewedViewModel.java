package com.nytreader.alsk.articlesList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.Menu;
import android.view.MenuItem;

import com.nytreader.alsk.AppNavigator;
import com.nytreader.alsk.R;
import com.nytreader.alsk.articlesList.ioc.ArticlesListScope;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

@ArticlesListScope
public class MostViewedViewModel extends ArticlesListViewModel {

    @VisibleForTesting
    public static final int COUNT_OF_NEWS_TO_SHOW = 10;

    @NonNull
    private final AppNavigator navigator;

    @Inject
    MostViewedViewModel(
            @NonNull Context context,
            @NonNull final ArticlesListDataSource dataSource,
            @NonNull AppNavigator navigator) {

        super(context, dataSource);
        this.navigator = navigator;
    }

    @Override
    protected Subscription tryLoadNextPage() {
        return dataSource.mostViewed()
                .subscribeOn(Schedulers.io())
                .take(COUNT_OF_NEWS_TO_SHOW)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(this::clearSubscription)
                .subscribe(articlesList::add, this::onError, this::onComplete);
    }

    private void onComplete() {
        // if no articles received
        if (articlesList.isEmpty()) {
            setBannerMessage(R.string.error_message_not_found);
        }
    }

    public void inflateMenu(Menu menu) {
        MenuItem menuItem = menu.add(R.string.search);
        menuItem.setIcon(R.drawable.ic_search_white_24px);
        menuItem.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        menuItem.setOnMenuItemClickListener(item -> {
            navigator.startSearchActivity();
            return true;
        });
    }
}
