package com.nytreader.alsk.nytreader.rest;

import com.nytreader.alsk.nytreader.BuildConfig;

import retrofit.Call;
import retrofit.http.GET;

public interface NytArticlesService {
    @GET("/svc/search/v2/articlesearch.json?apikey=" + BuildConfig.NYT_KEY)
    Call<ResponseModel> searchNews();
}
