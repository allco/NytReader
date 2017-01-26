package com.nytreader.alsk.articlesList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nytreader.alsk.articlesList.ioc.ArticlesListComponent;
import com.nytreader.alsk.articlesList.ioc.ArticlesListModule;
import com.nytreader.alsk.databinding.FragmentArticlesListBinding;
import com.nytreader.alsk.ioc.IoC;

public class ArticlesListFragment extends Fragment {

    private ArticlesListViewModel viewModel;

    /**
     * Use {@link #newInstance()} instead
     */
    @Deprecated
    public ArticlesListFragment() {
    }

    public static ArticlesListFragment newInstance() {
        @SuppressWarnings("deprecation")
        ArticlesListFragment fragment = new ArticlesListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ArticlesListComponent component = IoC.getInstance().getAppComponent().startArticlesList(new ArticlesListModule());
        viewModel = component.createModel();
        viewModel.loadItems();

        FragmentArticlesListBinding binding = FragmentArticlesListBinding.inflate(inflater, container, false);
        binding.frSwipeToRefresh.setOnRefreshListener(viewModel::loadItems);

        binding.setModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        viewModel.inflateMenu(menu, inflater);
    }
}
