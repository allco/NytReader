package com.nytreader.alsk.nytreader.articlesList.ioc;

import com.nytreader.alsk.nytreader.articlesList.ArticlesListViewModel;

import dagger.Subcomponent;

@ArticlesListScope
@Subcomponent(modules = ArticlesListModule.class)
public interface ArticlesListComponent {
    ArticlesListViewModel createModel();
}
