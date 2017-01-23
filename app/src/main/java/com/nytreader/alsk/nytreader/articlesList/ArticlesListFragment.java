package com.nytreader.alsk.nytreader.articlesList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nytreader.alsk.nytreader.articlesList.ioc.ArticlesListComponent;
import com.nytreader.alsk.nytreader.articlesList.ioc.ArticlesListModule;
import com.nytreader.alsk.nytreader.databinding.FragmentArticlesListBinding;
import com.nytreader.alsk.nytreader.ioc.IoC;

public class ArticlesListFragment extends Fragment {

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
        ArticlesListComponent component = IoC.getInstance().getAppComponent().startArticlesList(new ArticlesListModule());
        FragmentArticlesListBinding binding = FragmentArticlesListBinding.inflate(inflater, container, false);
        ArticlesListModel model = component.createModel();
        model.loadItems();
        binding.setModel(model);
        return binding.getRoot();
    }
}
