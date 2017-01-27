package com.nytreader.alsk.articlesList;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nytreader.alsk.BuildConfig;
import com.nytreader.alsk.TimeFormatter;
import com.nytreader.alsk.articlesList.ioc.ArticlesListScope;
import com.nytreader.alsk.rest.MostViewedDataModel;
import com.nytreader.alsk.rest.NytArticlesService;
import com.nytreader.alsk.rest.SearchedNewsDataModel;
import com.nytreader.alsk.utils.ui.recyclerview.LayoutProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import rx.Observable;

import static com.nytreader.alsk.ioc.AppModule.END_POINT_IMAGES;

@ArticlesListScope
public class ArticlesListDataSource {

    @NonNull
    private final NytArticlesService articlesService;
    @NonNull
    private final Provider<ArticlesListItemViewModel> listItemModelProvider;
    @NonNull
    private final TimeFormatter timeFormatter;
    @NonNull
    private final String endPoint;

    @Inject
    ArticlesListDataSource(
            @NonNull NytArticlesService articlesService,
            @NonNull Provider<ArticlesListItemViewModel> listItemModelProvider,
            @NonNull TimeFormatter timeFormatter,
            @Named(END_POINT_IMAGES) String endPoint
    ) {

        this.articlesService = articlesService;
        this.listItemModelProvider = listItemModelProvider;
        this.timeFormatter = timeFormatter;
        this.endPoint = endPoint;
    }

    public Observable<LayoutProvider> mostViewed() {
        return articlesService.mostViewed(BuildConfig.NYT_KEY)
                .filter(mostViewedDataModel -> {
                    List<MostViewedDataModel.Result> results = mostViewedDataModel == null ? null : mostViewedDataModel.getResults();
                    return results != null && !results.isEmpty();
                })
                .map(MostViewedDataModel::getResults)
                .flatMap(Observable::from)
                .map(this::mapToViewModel);
    }

    Observable<LayoutProvider> searchNews(String searchRequest, int page) {
        return articlesService.searchNews(searchRequest, page, BuildConfig.NYT_KEY)
                .filter(responseDataModel -> {
                    SearchedNewsDataModel.Response response = responseDataModel == null ? null : responseDataModel.getResponse();
                    List<SearchedNewsDataModel.Doc> docs = response == null ? null : response.getDocs();
                    return docs != null && !docs.isEmpty();
                })
                .map(responseDataModel -> responseDataModel.getResponse().getDocs())
                .flatMap(Observable::from)
                .map(this::mapToViewModel);
    }

    @NonNull
    private LayoutProvider mapToViewModel(@Nullable SearchedNewsDataModel.Doc doc) {

        SearchedNewsDataModel.Headline headline = doc == null ? null : doc.getHeadline();
        List<SearchedNewsDataModel.Multimedia> multimedia = doc == null ? null : doc.getMultimedia();
        SearchedNewsDataModel.Multimedia media = multimedia == null || multimedia.isEmpty() ? null : multimedia.get(0);

        String headerTitle = headline == null ? null : headline.getName();
        headerTitle = headerTitle != null ? headerTitle : (headline == null ? null : headline.getMain());

        String abstractTitle = doc == null ? null : doc.getSnippet();
        String imageCaption = media == null ? null : media.getCaption();
        String imageUrl = media == null ? null : endPoint + "/" + media.getUrl();
        String publishDate = timeFormatter.formatDate(doc == null ? null : doc.getPublicationDate());

        return listItemModelProvider.get().setData(headerTitle, abstractTitle, imageCaption, imageUrl, publishDate);
    }

    @NonNull
    private LayoutProvider mapToViewModel(@Nullable MostViewedDataModel.Result doc) {

        List<MostViewedDataModel.Medium> media = doc == null ? null : doc.getMedia();
        MostViewedDataModel.Medium medium = media == null || media.isEmpty() ? null : media.get(0);
        List<MostViewedDataModel.MediaMetadatum> mediaMetadata = medium == null ? null : medium.getMediaMetadata();
        MostViewedDataModel.MediaMetadatum mediaMetadatum = mediaMetadata == null || mediaMetadata.isEmpty() ? null : mediaMetadata.get(0);

        String headerTitle = doc == null ? null : doc.getTitle();
        String abstractTitle = doc == null ? null : doc.getSnippet();
        String imageCaption = medium == null ? null : medium.getCaption();
        String imageUrl = mediaMetadatum == null ? null : mediaMetadatum.getUrl();
        String publishDate = timeFormatter.formatDate(doc == null ? null : doc.getPublishedDate());

        return listItemModelProvider.get().setData(headerTitle, abstractTitle, imageCaption, imageUrl, publishDate);
    }
}
