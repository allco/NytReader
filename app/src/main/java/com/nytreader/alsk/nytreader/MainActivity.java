package com.nytreader.alsk.nytreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.nytreader.alsk.nytreader.articlesList.ArticlesListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.ten_most_read_articles));
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, ArticlesListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        new MenuInflater(this).inflate(R.menu.main_activity_menu, menu);
        menu.findItem(R.id.menu_do_search).setOnMenuItemClickListener(item -> {
            startSearch();
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void startSearch() {

    }
}
