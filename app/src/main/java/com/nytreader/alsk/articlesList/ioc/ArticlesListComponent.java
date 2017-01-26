package com.nytreader.alsk.articlesList.ioc;

import com.nytreader.alsk.articlesList.ArticlesListViewModel;

import dagger.Subcomponent;

@ArticlesListScope
@Subcomponent(modules = ArticlesListModule.class)
public interface ArticlesListComponent {
    ArticlesListViewModel createModel();
}
