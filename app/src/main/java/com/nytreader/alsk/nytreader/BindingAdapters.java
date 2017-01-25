package com.nytreader.alsk.nytreader;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nytreader.alsk.nytreader.ioc.IoC;
import com.nytreader.alsk.nytreader.utils.ThumbLoader;
import com.nytreader.alsk.nytreader.utils.recyclerview.LayoutProviderObservableList;
import com.nytreader.alsk.nytreader.utils.recyclerview.MultiTypeObserverDataBoundAdapter;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class BindingAdapters {

    @BindingAdapter("visibility")
    public static void bindVisibility(View view, boolean visibility) {
        view.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("imageUrl")
    public static void bindImageUrl(ImageView imageView, String imageUrl) {
        IoC.getInstance().getAppComponent().getThumbLoader().loadImage(imageView, imageUrl);
    }

    @BindingAdapter("layoutProviderItems")
    public static void bindRecyclerViewItems(RecyclerView recyclerView, @Nullable LayoutProviderObservableList layoutProviderItems) {

        // Bind with listOfItems only once
        if (recyclerView.getAdapter() != null) {
            return;
        }

        if (layoutProviderItems == null) {
            layoutProviderItems = new LayoutProviderObservableList();
        }

        ThumbLoader thumbLoader = IoC.getInstance().getAppComponent().getThumbLoader();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, final int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
                if (scrollState == SCROLL_STATE_IDLE) {
                    thumbLoader.resume();
                } else {
                    thumbLoader.pause();
                }
            }
        });

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MultiTypeObserverDataBoundAdapter adapter = new MultiTypeObserverDataBoundAdapter();
        adapter.setData(layoutProviderItems);
        recyclerView.setAdapter(adapter);
    }
}
