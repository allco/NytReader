package com.nytreader.alsk.articlesList.ioc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ArticlesListModule {

    public static final String TIME_FORMATTER_EXPECTED = "TIME_FORMATTER_EXPECTED";
    public static final String TIME_FORMATTER = "TIME_FORMATTER";

    @Provides
    @Named(TIME_FORMATTER_EXPECTED)
    public DateFormat provideExpectedTimeFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    }

    @Provides
    @Named(TIME_FORMATTER)
    public DateFormat provideTimeFormatter() {
        return SimpleDateFormat.getDateTimeInstance();
    }
}
