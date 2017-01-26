package com.nytreader.alsk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nytreader.alsk.articlesList.ArticlesListFragment;

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
}
