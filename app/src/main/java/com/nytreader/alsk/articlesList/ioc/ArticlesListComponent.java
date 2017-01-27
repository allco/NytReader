package com.nytreader.alsk.articlesList.ioc;

import com.nytreader.alsk.articlesList.MostViewedViewModel;
import com.nytreader.alsk.articlesList.SearchViewModel;

import dagger.Subcomponent;

@ArticlesListScope
@Subcomponent(modules = ArticlesListModule.class)
public interface ArticlesListComponent {
    MostViewedViewModel createMostViewedArticlesModel();
    SearchViewModel createSearchViewModel();
}
