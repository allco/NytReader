package com.nytreader.alsk.nytreader.articlesList;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.nytreader.alsk.nytreader.R;
import com.nytreader.alsk.nytreader.articlesList.ioc.ArticlesListScope;
import com.nytreader.alsk.nytreader.rest.NytArticlesService;
import com.nytreader.alsk.nytreader.rest.ResponseDataModel;
import com.nytreader.alsk.nytreader.utils.recyclerview.LayoutProviderObservableList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ArticlesListScope
public class ArticlesListViewModel {

    @NonNull
    public final LayoutProviderObservableList articlesList = new LayoutProviderObservableList();
    public final ObservableField<String> errorMessage = new ObservableField<>();

    DateFormat formatTo = SimpleDateFormat.getDateTimeInstance();
    DateFormat formatExpected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);

    private Context context;
    @NonNull
    private final NytArticlesService articlesService;
    @NonNull
    private final Provider<ArticlesListItemViewModel> listItemModelProvider;
    private Subscription subscriptionCall;

    @Inject
    public ArticlesListViewModel(@NonNull Context context, @NonNull final NytArticlesService articlesService, @NonNull final Provider<ArticlesListItemViewModel> listItemModelProvider) {
        this.context = context;
        this.articlesService = articlesService;
        this.listItemModelProvider = listItemModelProvider;
    }

    public void loadItems() {
        errorMessage.set(null);
        articlesList.clear();

        if (subscriptionCall != null) {
            subscriptionCall.unsubscribe();
        }

        subscriptionCall = articlesService.searchNews()
                .filter(responseDataModel -> {
                    ResponseDataModel.Response response = responseDataModel == null ? null : responseDataModel.getResponse();
                    List<ResponseDataModel.Doc> docs = response == null ? null : response.getDocs();
                    return docs != null && !docs.isEmpty();
                })
                .map(responseDataModel -> responseDataModel.getResponse().getDocs())
                .flatMap(Observable::from)
                .map(this::mapDocToListViewItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> subscriptionCall = null)
                .subscribe(articlesList::add, this::processError, this::checkForEmptyResult);
    }

    private void processError(Throwable throwable) {
        setErrorMessage(R.string.error_message_generic);
    }

    private void checkForEmptyResult() {
        if (articlesList.isEmpty()) {
            setErrorMessage(R.string.error_message_not_found);
        }
    }

    private void setErrorMessage(@StringRes final int resId) {
        errorMessage.set(context.getString(resId));
        articlesList.clear();
    }

    @NonNull
    private ArticlesListItemViewModel mapDocToListViewItem(ResponseDataModel.Doc doc) {

        ResponseDataModel.Headline headline = doc == null ? null : doc.getHeadline();
        List<ResponseDataModel.Multimedium> multimedia = doc == null ? null : doc.getMultimedia();
        ResponseDataModel.Multimedium media = multimedia == null || multimedia.isEmpty() ? null : multimedia.get(0);

        String headerTitle = headline == null ? null : headline.getName();
        String abstractTitle = doc == null ? null : doc.getSnippet();
        String imageCaption = media == null ? null : media.getCaption();
        String imageUrl = media == null ? null : media.getUrl();
        String publishDate = doc == null ? null : doc.getPubliationDate();

        // reformat a date
        if (!TextUtils.isEmpty(publishDate)) {
            try {
                publishDate = formatTo.format(formatExpected.parse(publishDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return listItemModelProvider.get().setData(headerTitle, abstractTitle, imageCaption, imageUrl, publishDate);
    }
}
