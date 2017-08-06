package gizwanda.popular_movies_stage1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by AdiGiz on 7/9/2017.
 */

public class MovieDetails extends AppCompatActivity {

    public final static String DETAIL_ACTIVITY_EXTRA ="We are going to the movie details";

    private TextView Title;
    private TextView Year;
    private TextView Duration;
    private TextView Rating;
    private TextView Description;
    private ImageView Poster;
    private String movieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);


        Intent intentFromMain = getIntent();
        if (intentFromMain.hasExtra(DETAIL_ACTIVITY_EXTRA)) {
            movieId = intentFromMain.getStringExtra(DETAIL_ACTIVITY_EXTRA);
        }

        Title = (TextView) findViewById(R.id.tv_movie_title);
        Year = (TextView) findViewById(R.id.tv_movie_year);
        Duration = (TextView) findViewById(R.id.tv_movie_duration);
        Rating = (TextView) findViewById(R.id.tv_movie_vote);
        Description = (TextView) findViewById(R.id.tv_movie_description);
        Poster = (ImageView) findViewById(R.id.iv_movie_poster);


        loadDetailMovieData(movieId);
    }

    private void loadDetailMovieData(String movieId) {
        URL detailMovieDataUrl = NetworkSettings.buildDetailMovieData(movieId, API.API_KEY);
        new FetchDetailMovieTask().execute(detailMovieDataUrl);
    }

    public class FetchDetailMovieTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... vURLs) {
            URL movieUrl = vURLs[0];
            String movieData = null;
            try {
                movieData = NetworkSettings.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieData;
        }

        @Override
        protected void onPostExecute(String vS) {
            super.onPostExecute(vS);
            try {
                JSONObject movieDataJSON = new JSONObject(vS);
                String title = movieDataJSON.getString("original_title");
                String year = movieDataJSON.getString("release_date").substring(0, 4);
                String duration = movieDataJSON.getString("runtime") + "min";
                String rating = movieDataJSON.getString("vote_average") + "/10";
                String description = movieDataJSON.getString("overview");
                String poster = movieDataJSON.getString("poster_path").substring(1);


                Title.setText(title);
                Year.setText(year);
                Duration.setText(duration);
                Rating.setText(rating);
                Description.setText(description);
                Picasso.with(MovieDetails.this).load(String.valueOf(NetworkSettings.buildImageUrl(poster))).into(Poster);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
