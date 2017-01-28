package com.nytreader.alsk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.nytreader.alsk.articlesList.SearchViewModel;
import com.nytreader.alsk.articlesList.ioc.ArticlesListComponent;
import com.nytreader.alsk.databinding.FragmentArticlesListBinding;
import com.nytreader.alsk.ioc.IoC;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArticlesListComponent component = IoC.getInstance().getAppComponent().startArticlesList();
        viewModel = component.createSearchViewModel();
        viewModel.inflateMenu(getSupportActionBarOrThrow());
        viewModel.reload();

        FragmentArticlesListBinding binding = FragmentArticlesListBinding.inflate(getLayoutInflater());
        binding.frSwipeToRefresh.setOnRefreshListener(viewModel::reload);
        binding.setModel(viewModel);

        setContentView(binding.getRoot());
        setTitle(getString(R.string.search));
        getSupportActionBarOrThrow().setHomeButtonEnabled(true);
    }

    @NonNull
    public ActionBar getSupportActionBarOrThrow() {
        ActionBar supportActionBar = super.getSupportActionBar();
        if (supportActionBar == null) {
            throw new IllegalStateException("Activity has to have an ActionBar. Check used theme.");
        }
        return supportActionBar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.tearDown();
        viewModel = null;
    }
}
