package com.nytreader.alsk.utils.ui.recyclerview;

import android.support.annotation.LayoutRes;

public interface LayoutProvider {
    @LayoutRes
    int getLayout();
    void onBind();
}
