package gizwanda.popular_movies_stage1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by AdiGiz on 7/9/2017.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG_NAME = MainActivity.class.getSimpleName();

    private RecyclerView mPopularMoviesRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private String stateOfSort;

    private TextView mErrorDisplayMessage;
    private ImageView mOffImageView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("sorted")) {
            stateOfSort = "popular";
        } else {
            stateOfSort = savedInstanceState.getString("sorted");
        }
        setContentView(R.layout.activity_main);

        mErrorDisplayMessage = (TextView) findViewById(R.id.error_message);
        mOffImageView = (ImageView) findViewById(R.id.cloud_off);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        mPopularMoviesRecyclerView = (RecyclerView) findViewById(R.id.popular_movies);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mPopularMoviesRecyclerView.setLayoutManager(layoutManager);
        mPopularMoviesRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter();
        mPopularMoviesRecyclerView.setAdapter(mMoviesAdapter);

        sortMovie(stateOfSort);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sorted", stateOfSort);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mMoviesAdapter.setMovieData(null);
                stateOfSort = "popular";
                sortMovie(stateOfSort);
            case R.id.action_popular:
                stateOfSort = getString(R.string.popular);
                sortMovie(stateOfSort);
                break;
            case R.id.action_now_playing:
                stateOfSort = getString(R.string.now_playing);
                sortMovie(stateOfSort);
                break;
            case R.id.action_top_rated:
                stateOfSort = getString(R.string.top_rated);
                sortMovie(stateOfSort);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortMovie(String sortBy) {
        showMovieDataView();
        URL sortedMovieUrl = NetworkSettings.buildSortUrl(sortBy, API.API_KEY);
        if (isOnline()) {
            new FetchMovieTask().execute(sortedMovieUrl);
        } else {
            showErrorMessage();
        }
    }

    private void showMovieDataView() {
        mErrorDisplayMessage.setVisibility(View.INVISIBLE);
        mOffImageView.setVisibility(View.INVISIBLE);
        mPopularMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mPopularMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorDisplayMessage.setVisibility(View.VISIBLE);
        mOffImageView.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... vURLs) {
            URL movieUrl = vURLs[0];
            String movieResult = null;
            try {
                movieResult = NetworkSettings.getResponseFromHttpUrl(movieUrl);
            } catch (IOException vE) {
                vE.printStackTrace();
            }
            return movieResult;
        }

        @Override
        protected void onPostExecute(String movieData) {
            super.onPostExecute(movieData);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            try {
                JSONObject root = new JSONObject(movieData);
                JSONArray jsonArray = root.getJSONArray("results");
                mMoviesAdapter.setMovieData(jsonArray);
            } catch (Exception vE) {
                vE.printStackTrace();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
