package com.nytreader.alsk.articlesList;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nytreader.alsk.articlesList.ioc.ArticlesListScope;
import com.nytreader.alsk.rest.NytArticlesService;
import com.nytreader.alsk.rest.ResponseDataModel;
import com.nytreader.alsk.ui.recyclerview.LayoutProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rx.Observable;

import static com.nytreader.alsk.articlesList.ioc.ArticlesListModule.TIME_FORMATTER;
import static com.nytreader.alsk.articlesList.ioc.ArticlesListModule.TIME_FORMATTER_EXPECTED;

@ArticlesListScope
public class ArticlesListDataSource {

    @NonNull
    private final NytArticlesService articlesService;
    @NonNull
    private final Provider<ArticlesListItemViewModel> listItemModelProvider;

    private final DateFormat formatTo;
    private final DateFormat formatExpected;

    @Inject
    ArticlesListDataSource(
            @NonNull NytArticlesService articlesService,
            @NonNull Provider<ArticlesListItemViewModel> listItemModelProvider,
            @NonNull @Named(TIME_FORMATTER_EXPECTED) DateFormat formatExpected,
            @NonNull @Named(TIME_FORMATTER) DateFormat formatTo) {

        this.articlesService = articlesService;
        this.listItemModelProvider = listItemModelProvider;
        this.formatExpected = formatExpected;
        this.formatTo = formatTo;
    }

    Observable<LayoutProvider> loadItems(String searchRequest, int page) {
        return articlesService.searchNews(searchRequest, page)
                .filter(responseDataModel -> {
                    ResponseDataModel.Response response = responseDataModel == null ? null : responseDataModel.getResponse();
                    List<ResponseDataModel.Doc> docs = response == null ? null : response.getDocs();
                    return docs != null && !docs.isEmpty();
                })
                .map(responseDataModel -> responseDataModel.getResponse().getDocs())
                .flatMap(Observable::from)
                .map(this::mapDocToListViewItem);
    }

    @NonNull
    private LayoutProvider mapDocToListViewItem(ResponseDataModel.Doc doc) {

        ResponseDataModel.Headline headline = doc == null ? null : doc.getHeadline();
        List<ResponseDataModel.Multimedia> multimedia = doc == null ? null : doc.getMultimedia();
        ResponseDataModel.Multimedia media = multimedia == null || multimedia.isEmpty() ? null : multimedia.get(0);

        String headerTitle = headline == null ? null : headline.getName();
        headerTitle = headerTitle != null ? headerTitle : (headline == null ? null : headline.getMain());

        String abstractTitle = doc == null ? null : doc.getSnippet();
        String imageCaption = media == null ? null : media.getCaption();
        String imageUrl = media == null ? null : media.getUrl();
        String publishDate = doc == null ? null : doc.getPubliationDate();

        // reformat the date
        if (!TextUtils.isEmpty(publishDate)) {
            try {
                publishDate = formatTo.format(formatExpected.parse(publishDate));
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }

        return listItemModelProvider.get().setData(headerTitle, abstractTitle, imageCaption, imageUrl, publishDate);
    }
}
