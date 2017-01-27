package com.nytreader.alsk.ioc;

import com.nytreader.alsk.articlesList.ioc.ArticlesListComponent;
import com.nytreader.alsk.utils.ThumbLoader;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    ArticlesListComponent startArticlesList();
    ThumbLoader getThumbLoader();
}
