package com.nytreader.alsk.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import rx.functions.Action0;

public class SearchView extends android.support.v7.widget.SearchView {

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Nullable
    private Action0 listenerExpand;
    @Nullable
    private Action0 listenerCollapse;

    public void setListenerExpand(Action0 listenerExpand) {
        this.listenerExpand = listenerExpand;
    }

    public void setListenerCollapse(Action0 listenerCollapse) {
        this.listenerCollapse = listenerCollapse;
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        if (listenerExpand != null) {
            listenerExpand.call();
        }
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        if (listenerCollapse != null) {
            listenerCollapse.call();
        }
    }
}
