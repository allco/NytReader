package com.nytreader.alsk.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.nytreader.alsk.R;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.nytreader.alsk.ioc.AppModule.THUMB_SIZE;

@Singleton
public class ThumbLoader {

    @NonNull
    private Context context;
    private int sizeThumb;

    @Inject
    public ThumbLoader(@NonNull Context context, @Named(THUMB_SIZE) final int sizeThumb) {
        this.context = context;
        this.sizeThumb = sizeThumb;
    }

    public void loadImage(@NonNull final ImageView imageView, @NonNull final String imageUrl) {
        Picasso.with(context)
                .load(imageUrl)
                .error(R.drawable.thumb_stub)
                .placeholder(R.drawable.thumb_stub)
                .resize(sizeThumb, sizeThumb)
                .centerCrop()
                .tag(context)
                .into(imageView);
    }

    public void pause() {
        Picasso picasso = Picasso.with(context);
        picasso.pauseTag(context);
    }

    public void resume() {
        Picasso.with(context).resumeTag(context);
    }
}
