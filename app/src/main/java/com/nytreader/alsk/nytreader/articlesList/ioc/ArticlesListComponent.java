package com.nytreader.alsk.nytreader.articlesList.ioc;

import com.nytreader.alsk.nytreader.articlesList.ArticlesListModel;

import dagger.Subcomponent;

@ArticlesListScope
@Subcomponent(modules = ArticlesListModule.class)
public interface ArticlesListComponent {
    ArticlesListModel createModel();
}
