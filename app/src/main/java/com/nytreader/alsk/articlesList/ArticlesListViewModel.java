package com.nytreader.alsk.articlesList;

import android.content.Context;
import android.databinding.ObservableField;
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
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@ArticlesListScope
public class ArticlesListViewModel {
    private static final String TAG = "ArticlesListViewModel";
    public static final int HTTP_TOO_MANY_REQUESTS = 429;

    @NonNull
    public final LayoutProviderObservableList articlesList = new LayoutProviderObservableList();
    public final ObservableField<String> errorMessage = new ObservableField<>();

    private Context context;
    @NonNull
    private final ArticlesListDataSource dataSource;
    @NonNull

    @Nullable
    private Subscription subscriptionCall;
    private int page = 0;

    @Inject
    public ArticlesListViewModel(
            @NonNull Context context,
            @NonNull final ArticlesListDataSource dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void loadItems() {
        errorMessage.set(null);
        articlesList.clear();

        if (subscriptionCall != null) {
            subscriptionCall.unsubscribe();
        }

        page = 0;
        tryLoadNextPage();
    }

    private void tryLoadNextPage() {

        if (subscriptionCall != null) {
            return;
        }

        subscriptionCall = dataSource.loadItems(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<LayoutProvider>() {
                    @Override
                    public void call(LayoutProvider layoutProvider) {
                        if (articlesList.isEmpty()) {
                            return;
                        }
                        int lastIndex = articlesList.size() - 1;
                        LayoutProvider lastItem = articlesList.get(lastIndex);
                        if (!(lastItem instanceof ArticlesListItemViewModel)) {
                            articlesList.remove(lastItem);
                        }
                    }
                })
                .doOnTerminate(() -> subscriptionCall = null)
                .subscribe(articlesList::add, this::processError, this::onComplete);
    }

    private void onComplete() {

        if (articlesList.isEmpty()) {
            setErrorMessage(R.string.error_message_not_found);
            return;
        }

        articlesList.add(new LayoutProvider() {
            @Override
            public int getLayout() {
                return R.layout.fragment_articles_list_item_loading;
            }

            @Override
            public void onBind() {
                tryLoadNextPage();
            }
        });

        page++;
    }

    private void processError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            if (((HttpException) throwable).code() == HTTP_TOO_MANY_REQUESTS) {
                setErrorMessage(R.string.error_message_too_many_requests);
                return;
            }
        }

        setErrorMessage(R.string.error_message_generic);
    }

    private void setErrorMessage(@StringRes final int resId) {
        errorMessage.set(context.getString(resId));
        articlesList.clear();
    }

    void inflateMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_do_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(context.getString(R.string.search_hint));
        searchView.setListenerCollapse(this::onSearchStarted);
        searchView.setListenerExpand(this::onSearchCanceled);
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

    }

    private void onSearchCanceled() {

    }

    private void onSearchStarted() {

    }
}
