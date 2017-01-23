package com.nytreader.alsk.nytreader.articlesList;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.nytreader.alsk.nytreader.R;
import com.nytreader.alsk.nytreader.articlesList.ioc.ArticlesListScope;
import com.nytreader.alsk.nytreader.rest.NytArticlesService;
import com.nytreader.alsk.nytreader.rest.ResponseModel;
import com.nytreader.alsk.nytreader.utils.recyclerview.LayoutProvider;
import com.nytreader.alsk.nytreader.utils.recyclerview.LayoutProviderObservableList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

@ArticlesListScope
public class ArticlesListModel {

    @NonNull
    public final LayoutProviderObservableList articlesList = new LayoutProviderObservableList();
    public final ObservableField<String> errorMessage = new ObservableField<>();

    private Context context;
    @NonNull
    private final NytArticlesService articlesService;
    @NonNull
    private final Provider<ArticlesListItemModel> listItemModelProvider;

    @Nullable
    private Call<ResponseModel> call;

    @Inject
    public ArticlesListModel(@NonNull Context context, @NonNull final NytArticlesService articlesService, @NonNull final Provider<ArticlesListItemModel> listItemModelProvider) {
        this.context = context;
        this.articlesService = articlesService;
        this.listItemModelProvider = listItemModelProvider;
    }

    public void loadItems() {
        errorMessage.set(null);
        call = articlesService.searchNews();
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(final Response<ResponseModel> response, final Retrofit retrofit) {
                call = null;
                switch (response.code()) {
                    case HTTP_OK:
                        articlesList.addAll(mapResponseModel(response.body()));
                        break;
                    case HTTP_NOT_FOUND:
                        setErrorMessage(R.string.error_message_not_found);
                        break;
                    default:
                        setErrorMessage(R.string.error_message_generic);
                        break;
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                call = null;
                setErrorMessage(R.string.error_message_generic);
            }
        });
    }

    private void setErrorMessage(@StringRes final int resId) {
        errorMessage.set(context.getString(resId));
        articlesList.clear();
    }

    @NonNull
    public List<LayoutProvider> mapResponseModel(@Nullable ResponseModel responseModel) {
        ResponseModel.Response response = responseModel == null ? null : responseModel.getResponse();
        List<ResponseModel.Doc> docs = response == null ? null : response.getDocs();
        if (docs == null || docs.isEmpty()) {
            return Collections.emptyList();
        }

        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        ArrayList<LayoutProvider> result = new ArrayList<>(docs.size());
        for (ResponseModel.Doc doc : docs) {

            ResponseModel.Headline headline = doc == null ? null : doc.getHeadline();
            List<ResponseModel.Multimedium> multimedia = doc == null ? null : doc.getMultimedia();
            ResponseModel.Multimedium media = multimedia == null || multimedia.isEmpty() ? null : multimedia.get(0);

            String headerTitle = headline == null ? null : headline.getName();
            String abstractTitle = doc == null ? null : doc.get_abstract();
            String imageCaption = media == null ? null : media.getCaption();
            String imageUrl = media == null ? null : media.getUrl();
            String publishDate = doc == null ? null : doc.getPub_date();

            // reformat a date
            if (!TextUtils.isEmpty(publishDate)) {
                try {
                    publishDate = format.format(format.parse(publishDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            result.add(listItemModelProvider.get().setData(headerTitle, abstractTitle, imageCaption, imageUrl, publishDate));
        }

        return result;
    }
}
