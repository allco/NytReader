package com.nytreader.alsk.ui.recyclerview;

import android.databinding.ViewDataBinding;

import com.nytreader.alsk.BR;

import java.util.List;

public class MultiTypeObserverDataBoundAdapter extends BaseObserverDataBoundAdapter<LayoutProvider, ViewDataBinding> {

    public MultiTypeObserverDataBoundAdapter() {
    }

    @Override
    protected void bindItem(GenericBindingViewHolder<ViewDataBinding> holder, int position, List<Object> payloads) {
        LayoutProvider item = getItem(position);
        holder.binding.setVariable(BR.model, item);
        item.onBind();
    }

    @Override
    protected int getItemLayoutId(int position) {
        return getItem(position).getLayout();
    }
}
