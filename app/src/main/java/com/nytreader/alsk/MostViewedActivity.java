package com.nytreader.alsk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.nytreader.alsk.articlesList.MostViewedViewModel;
import com.nytreader.alsk.articlesList.ioc.ArticlesListComponent;
import com.nytreader.alsk.databinding.FragmentArticlesListBinding;
import com.nytreader.alsk.ioc.IoC;

public class MostViewedActivity extends AppCompatActivity {

    private MostViewedViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArticlesListComponent component = IoC.getInstance().getAppComponent().startArticlesList();
        viewModel = component.createMostViewedArticlesModel();
        viewModel.reload();

        FragmentArticlesListBinding binding = FragmentArticlesListBinding.inflate(getLayoutInflater());
        binding.frSwipeToRefresh.setOnRefreshListener(viewModel::reload);
        binding.setModel(viewModel);

        setContentView(binding.getRoot());
        setTitle(getString(R.string.ten_most_read_articles));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.tearDown();
        viewModel = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        viewModel.inflateMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }
}
