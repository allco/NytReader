package com.nytreader.alsk.nytreader.ioc;

import com.nytreader.alsk.nytreader.articlesList.ioc.ArticlesListComponent;
import com.nytreader.alsk.nytreader.articlesList.ioc.ArticlesListModule;
import com.nytreader.alsk.nytreader.utils.ThumbLoader;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    ArticlesListComponent startArticlesList(ArticlesListModule module);
    ThumbLoader getThumbLoader();
}
