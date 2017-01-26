package com.nytreader.alsk.articlesList;

import android.content.Context;
import android.databinding.ObservableField;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nytreader.alsk.R;
import com.nytreader.alsk.articlesList.ioc.ArticlesListScope;
import com.nytreader.alsk.ui.SearchView;
import com.nytreader.alsk.ui.recyclerview.LayoutProvider;
import com.nytreader.alsk.ui.recyclerview.LayoutProviderObservableList;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

@ArticlesListScope
public class ArticlesListViewModel {
    private static final String TAG = "ArticlesListViewModel";
    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    // defined by NYT API
    private static final int EXPECTED_PAGE_SIZE = 10;
    private static final int MAX_PAGE_NUMBER = 120;
    public static final int START_SEARCH_DELAY = 500;

    @NonNull
    public final LayoutProviderObservableList articlesList = new LayoutProviderObservableList();
    public final ObservableField<String> errorMessage = new ObservableField<>();

    @NonNull
    private Context context;
    @NonNull
    private final ArticlesListDataSource dataSource;
    @NonNull
    private final Handler handler;

    @Nullable
    private Subscription subscriptionCall;
    private int counterAddedElements = 0;
    private int pageNumber = 0;

    @Nullable
    private String searchRequest;

    @Inject
    ArticlesListViewModel(@NonNull Context context, @NonNull final ArticlesListDataSource dataSource, @NonNull Handler handler) {
        this.context = context;
        this.dataSource = dataSource;
        this.handler = handler;
    }

    void reloadArticles() {
        if (subscriptionCall != null) {
            subscriptionCall.unsubscribe();
        }

        if ("".equals(searchRequest)) {
            setBannerMessage(R.string.error_message_empty_search_request);
            return;
        }

        errorMessage.set(null);
        articlesList.clear();
        pageNumber = 0;
        subscriptionCall = null;
        tryLoadNextPage();
    }

    private void tryLoadNextPage() {
        // do nothing, if there is a unfinished request
        if (subscriptionCall != null) {
            return;
        }

        counterAddedElements = 0;
        subscriptionCall = dataSource.loadItems(searchRequest, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    subscriptionCall = null;
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

    private void onError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).code();
            switch (code) {
                case HTTP_TOO_MANY_REQUESTS:
                    setBannerMessage(R.string.error_message_too_many_requests);
                    return;
                case HTTP_FORBIDDEN:
                    setBannerMessage(R.string.error_message_forbidden);
                    return;
                default:
                    setBannerMessage(context.getString(R.string.error_message_generic) + "\n(code: " + code + ")");
                    break;
            }
        }

        if (articlesList.isEmpty()) {
            setBannerMessage(R.string.error_message_generic);
        }
    }

    private void setBannerMessage(@StringRes final int resId) {
        setBannerMessage(context.getString(resId));
    }

    private void setBannerMessage(String message) {
        errorMessage.set(message);
        articlesList.clear();
    }

    void inflateMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_do_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(context.getString(R.string.search_hint));
        searchView.setListenerCollapse(this::onSearchCanceled);
        searchView.setListenerExpand(this::onSearchStarted);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onQueryChange(newText);
                return false;
            }
        });
    }

    private void onQueryChange(String request) {

        if (searchRequest == null && request.equals("")) {
            onSearchStarted();
            return;
        }

        if (request == null || request.equals(searchRequest)) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            searchRequest = request;
            reloadArticles();
        }, START_SEARCH_DELAY);
    }

    private void onSearchCanceled() {
        searchRequest = null;
        reloadArticles();
    }

    private void onSearchStarted() {
        searchRequest = "";
        reloadArticles();
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
        if (subscriptionCall != null) {
            subscriptionCall.unsubscribe();
        }

        subscriptionCall = null;
        handler.removeCallbacksAndMessages(null);
    }
}
