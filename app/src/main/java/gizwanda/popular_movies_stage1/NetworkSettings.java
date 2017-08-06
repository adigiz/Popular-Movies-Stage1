package gizwanda.popular_movies_stage1;

/**
 * Created by AdiGiz on 7/9/2017.
 */
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkSettings {
    public static URL buildSortUrl(String sort, String api_key){
        Uri buildUri = Uri.parse(API.API_URL).buildUpon()
                .appendPath(sort)
                .appendQueryParameter("api_key", api_key)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildImageUrl(String posterPath){
        Uri buildUri = Uri.parse(API.IMAGE_URL).buildUpon()
                .appendPath(posterPath)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }



    public static URL buildDetailMovieData(String movieId, String api_key){
        Uri buildUri = Uri.parse(API.API_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter("api_key", api_key)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else{
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
