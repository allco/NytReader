package com.nytreader.alsk.rest;

import com.nytreader.alsk.BuildConfig;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface NytArticlesService {
    @GET("/svc/search/v2/articlesearch.json?apikey=" + BuildConfig.NYT_KEY)
    Observable<ResponseDataModel> searchNews(@Query("page") int pageNumber);
}
