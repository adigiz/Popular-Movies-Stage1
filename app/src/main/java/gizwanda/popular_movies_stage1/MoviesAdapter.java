package gizwanda.popular_movies_stage1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by AdiGiz on 7/9/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{
    private static final String TAG_NAME = MoviesAdapter.class.getSimpleName();
    private JSONArray mMoviesJSONArray;

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.movie_list, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        String posterPath;
        try {
            holder.mLoadingProgressBar.setVisibility(View.GONE);
            holder.mMovieImageView.setVisibility(View.VISIBLE);
            JSONObject movie = mMoviesJSONArray.getJSONObject(position);
            posterPath = movie.getString("poster_path");
            Picasso.with(holder.context)
                    .load(String.valueOf(NetworkSettings.buildImageUrl(posterPath.substring(1))))
                    .into(holder.mMovieImageView);
        } catch (Exception vE) {
            vE.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesJSONArray == null) {
            return 0;
        } else {
            return mMoviesJSONArray.length();
        }

    }

    public void setMovieData(JSONArray vMovieData) {
        mMoviesJSONArray = vMovieData;
        notifyDataSetChanged();

    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Context context = null;
        public final ImageView mMovieImageView;
        public final ProgressBar mLoadingProgressBar;

        public MoviesAdapterViewHolder(View vView) {
            super(vView);
            mMovieImageView = vView.findViewById(R.id.iv_movie);
            mLoadingProgressBar = vView.findViewById(R.id.pb_loading);
            context = vView.getContext();
            vView.setOnClickListener(this);

        }

        @Override
        public void onClick(View vView) {
            Intent detailIntent = new Intent(context, MovieDetails.class);
            JSONObject movieDetailJSON;
            try {
                movieDetailJSON = mMoviesJSONArray.getJSONObject(getAdapterPosition());
                detailIntent.putExtra(MovieDetails.DETAIL_ACTIVITY_EXTRA, movieDetailJSON.getString("id"));

            } catch (Exception vE) {
                vE.printStackTrace();
            }
            context.startActivity(detailIntent);
        }
    }
}
