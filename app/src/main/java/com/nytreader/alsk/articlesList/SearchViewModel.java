package com.nytreader.alsk.articlesList;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;

import com.nytreader.alsk.R;
import com.nytreader.alsk.articlesList.ioc.ArticlesListScope;
import com.nytreader.alsk.utils.ui.recyclerview.LayoutProvider;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;
import static com.nytreader.alsk.rest.NytArticlesService.EXPECTED_PAGE_SIZE;
import static com.nytreader.alsk.rest.NytArticlesService.MAX_PAGE_NUMBER;

@ArticlesListScope
public class SearchViewModel extends ArticlesListViewModel {
    private static final String TAG = "MostViewedViewModel";

    // defined by NYT API
    public static final int START_SEARCH_DELAY = 500;

    @NonNull
    private final Handler handler;

    private int counterAddedElements = 0;
    private int pageNumber = 0;

    @Nullable
    private String searchRequest;

    @Inject
    SearchViewModel(@NonNull Context context, @NonNull final ArticlesListDataSource dataSource, @NonNull Handler handler) {
        super(context, dataSource);
        this.handler = handler;
    }

    @Override
    public void reload() {
        pageNumber = 0;
        super.reload();
    }

    @Override
    protected Subscription tryLoadNextPage() {

        // do nothing, if there is a unfinished request
        if (!isIdle()) {
            return null;
        }

        // if searchRequest is empty show "Found nothing" banner.
        if (isEmpty(searchRequest)) {
            setBannerMessage(R.string.error_message_empty_search_request);
            return null;
        }

        counterAddedElements = 0;
        return dataSource.searchNews(searchRequest, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    cancelSubscription();
                    removeLoadingItem();
                })
                .subscribe(this::onNext, this::onError, this::onComplete);
    }

    private void onNext(LayoutProvider articleItem) {
        // remove "Loading" item before adding the first article from the response
        if (counterAddedElements == 0) {
            removeLoadingItem();
        }
        articlesList.add(articleItem);
        counterAddedElements++;
    }

    private void onComplete() {
        // if no articles received
        if (articlesList.isEmpty()) {
            setBannerMessage(R.string.error_message_not_found);
            return;
        }

        // if there are more articles available for fetching
        if (counterAddedElements >= EXPECTED_PAGE_SIZE && pageNumber <= MAX_PAGE_NUMBER) {
            // allow to load next page
            pageNumber++;
            // try to add "Loading" item if it does not present
            int loadingItemIndex = getLoadingItemIndex();
            if (loadingItemIndex < 0) {
                articlesList.add(new LayoutProvider() {
                    @Override
                    public int getLayout() {
                        return R.layout.fragment_articles_list_item_loading;
                    }

                    @Override
                    public void onBind() {
                        // the appearing on screen of "Loading" item cases loading next page
                        tryLoadNextPage();
                    }
                });
            }
        }
    }

    public void inflateMenu(@NonNull ActionBar actionBar) {

        SearchView searchView = new SearchView(actionBar.getThemedContext());
        searchView.setQueryHint(context.getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handler.removeCallbacksAndMessages(null);
                reload();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String request) {
                if (request == null || request.equals(searchRequest)) {
                    return false;
                }
                cancelSubscription();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    searchRequest = request;
                    reload();
                }, START_SEARCH_DELAY);
                return true;
            }
        });

        actionBar.setCustomView(searchView);
        actionBar.setDisplayShowCustomEnabled(true);
        searchView.onActionViewExpanded();
    }

    private int getLoadingItemIndex() {
        if (!articlesList.isEmpty()) {
            int lastIndex = articlesList.size() - 1;
            LayoutProvider lastItem = articlesList.get(lastIndex);
            if (lastItem.getLayout() == R.layout.fragment_articles_list_item_loading) {
                return lastIndex;
            }
        }
        return -1;
    }

    private void removeLoadingItem() {
        int loadingItemIndex = getLoadingItemIndex();
        if (loadingItemIndex >= 0) {
            articlesList.remove(loadingItemIndex);
        }
    }

    public void tearDown() {
        super.tearDown();
        handler.removeCallbacksAndMessages(null);
    }
}
