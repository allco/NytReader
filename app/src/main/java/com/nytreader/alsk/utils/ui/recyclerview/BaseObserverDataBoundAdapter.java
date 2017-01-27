package com.nytreader.alsk.utils.ui.recyclerview;

import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

public abstract class BaseObserverDataBoundAdapter<DATA, BINDING extends ViewDataBinding> extends BaseDataBoundAdapter<BINDING> {

    private ObservableList<DATA> data;

    public BaseObserverDataBoundAdapter() {
    }

    public void setData(ObservableList<DATA> data) {
        this.data = data;
        this.data.addOnListChangedCallback(createChangesListener(new WeakReference<>(this)));
    }

    public ObservableList<DATA> getObservableList() {
        return data;
    }

    DATA getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static <DATA, BINDING extends ViewDataBinding> ObservableList.OnListChangedCallback<ObservableList<DATA>> createChangesListener(final WeakReference<BaseObserverDataBoundAdapter<DATA, BINDING>> adapterRef) {
        return new ObservableList.OnListChangedCallback<ObservableList<DATA>>() {
            @Override
            public void onChanged(ObservableList<DATA> sender) {
                BaseObserverDataBoundAdapter adapter = getAdapter(sender);
                if (adapter == null) {
                    return;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<DATA> sender, int positionStart, int itemCount) {
                BaseObserverDataBoundAdapter adapter = getAdapter(sender);
                if (adapter == null) {
                    return;
                }
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<DATA> sender, int positionStart, int itemCount) {
                BaseObserverDataBoundAdapter adapter = getAdapter(sender);
                if (adapter == null) {
                    return;
                }
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<DATA> sender, int fromPosition, int toPosition, int itemCount) {
                BaseObserverDataBoundAdapter adapter = getAdapter(sender);
                if (adapter == null) {
                    return;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<DATA> sender, int positionStart, int itemCount) {
                BaseObserverDataBoundAdapter adapter = getAdapter(sender);
                if (adapter == null) {
                    return;
                }
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Nullable
            private BaseObserverDataBoundAdapter getAdapter(final ObservableList<DATA> sender) {
                BaseObserverDataBoundAdapter adapter = adapterRef == null ? null : adapterRef.get();
                if (adapter == null) {
                    return null;
                }

                if (sender == null) {
                    return null;
                }

                if (adapter.data != sender) {
                    sender.removeOnListChangedCallback(this);
                    return null;
                }

                return adapter;
            }
        };
    }
}