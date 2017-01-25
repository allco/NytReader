package com.nytreader.alsk.nytreader.rest;

import com.nytreader.alsk.nytreader.BuildConfig;

import retrofit2.http.GET;
import rx.Observable;

public interface NytArticlesService {
    @GET("/svc/search/v2/articlesearch.json?apikey=" + BuildConfig.NYT_KEY)
    Observable<ResponseDataModel> searchNews();
}
