package com.nytreader.alsk.nytreader.utils.recyclerview;

import android.databinding.ViewDataBinding;

import com.nytreader.alsk.nytreader.BR;

import java.util.List;

public class MultiTypeObserverDataBoundAdapter extends BaseObserverDataBoundAdapter<LayoutProvider, ViewDataBinding> {

    public MultiTypeObserverDataBoundAdapter() {
    }

    @Override
    protected void bindItem(GenericBindingViewHolder<ViewDataBinding> holder, int position, List<Object> payloads) {
        holder.binding.setVariable(BR.model, getItem(position));
    }

    @Override
    protected int getItemLayoutId(int position) {
        return getItem(position).getLayout();
    }
}
