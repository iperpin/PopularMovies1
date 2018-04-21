package com.example.movies.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.movies.popularmovies.objects.MovieObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private static final String LOG_TAG = "MainActivity";
    private static final int NUM_COLUMNS_RECYCLERVIEW = 2;
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noInternetTextView;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = findViewById(R.id.swipe_layout);
        recyclerView = findViewById(R.id.rvMovies);
        noInternetTextView = findViewById(R.id.tv_no_internet);

        noInternetTextView.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUM_COLUMNS_RECYCLERVIEW));
        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    public void requestMovies(String url) {
        Log.d(LOG_TAG, "New petition to: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        noInternetTextView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String bodyResponse = response.body().string();
                final List<MovieObject> movieObjects = Utils.parseMoviesJSON(bodyResponse);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noInternetTextView.setVisibility(View.INVISIBLE);
                        adapter.update(movieObjects);
                    }
                });
            }
        });
    }

    @Override
    public void onListItemClick(int clickedItemIndex, MovieObject movie) {
        Log.d(LOG_TAG, movie.toString());
        Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
        intent.putExtra(getString(R.string.intent_movie_object), movie);
        startActivity(intent);
    }

    private void refreshItems() {
        requestMovies(getString(R.string.url_base) + Utils.getUrlSpinnerChoice(MainActivity.this)
                + getString(R.string.api_key_tag) + getString(R.string.api_key));
        swipeLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);

        MenuItem item = menu.findItem(R.id.actionbar_spinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_items, R.layout.spinner_single_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);

        spinner.setAdapter(adapter);
        int lastSpinnerPosition = Utils.LoadPreferencesInt(MainActivity.this, getString(R.string.spinner_position), 0);
        spinner.setSelection(lastSpinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                requestMovies(getString(R.string.url_base) + Utils.getUrlChoice(MainActivity.this, position)
                        + getString(R.string.api_key_tag) + getString(R.string.api_key));
                Utils.savePrefsInt(MainActivity.this, getString(R.string.spinner_position), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        return true;
    }
}
