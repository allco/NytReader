package com.nytreader.alsk.rest;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface NytArticlesService {

    int HTTP_TOO_MANY_REQUESTS = 429;
    int EXPECTED_PAGE_SIZE = 10;
    int MAX_PAGE_NUMBER = 120;

    @GET("/svc/search/v2/articlesearch.json")
    Observable<SearchedNewsDataModel> searchNews(@Query("q") String searchRequest, @Query("page") int pageNumber, @Query("apikey") String apiKey);

    @GET("/svc/mostpopular/v2/mostviewed/all-sections/30.json")
    Observable<MostViewedDataModel> mostViewed(@Query("apikey") String apiKey);
}
