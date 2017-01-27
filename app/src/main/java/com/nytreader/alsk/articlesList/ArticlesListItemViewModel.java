package com.nytreader.alsk.articlesList;

import android.databinding.ObservableField;

import com.nytreader.alsk.R;
import com.nytreader.alsk.utils.ui.recyclerview.LayoutProvider;

import javax.inject.Inject;

public class ArticlesListItemViewModel implements LayoutProvider {

    public final ObservableField<String> headerTitle = new ObservableField<>();
    public final ObservableField<String> abstractTitle = new ObservableField<>();
    public final ObservableField<String> publicationDate = new ObservableField<>();
    public final ObservableField<String> imageCaption = new ObservableField<>();
    public final ObservableField<String> imageUrl = new ObservableField<>();

    @Inject
    ArticlesListItemViewModel() {
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_articles_list_item;
    }

    @Override
    public void onBind() {

    }

    ArticlesListItemViewModel setData(final String headerTitle, final String abstractTitle, final String imageCaption, final String imageUrl, final String publicationDate) {
        this.publicationDate.set(publicationDate);
        this.abstractTitle.set(abstractTitle);
        this.imageCaption.set(imageCaption);
        this.headerTitle.set(headerTitle);
        this.imageUrl.set(imageUrl);
        return this;
    }
}
