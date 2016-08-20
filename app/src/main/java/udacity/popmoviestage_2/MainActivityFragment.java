package udacity.popmoviestage_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import udacity.popmoviestage_2.data.MovieContract;

/**
 * Created by guoecho on 2016/8/12.
 */
public class MainActivityFragment extends Fragment {


    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<Movie> mMovie;
    private String mBase_URL = "http://api.themoviedb.org/3/movie/";
    private String mSort = null;
    private String mApi_key = "?api_key=" + BuildConfig.MOVIES_TMDB_API_KEY;

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ArrayList<Movie> mFavoriteMovie;


    public MainActivityFragment() {

    }

    public interface Callback {
        void onItemSelected(Movie movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_grid_view, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_favorite) {

            queryFavoriteMovies();

            mGridAdapter.setGridData(mFavoriteMovie);
            Toast.makeText(getContext(), "this is a favorite list !", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);

        mMovie = new ArrayList<>();
        mFavoriteMovie = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.fragment_main, mMovie);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        return view;
    }


    public void updateMovies() {
        mGridAdapter.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mSort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        String fullPath = mBase_URL + mSort + mApi_key;
        AsyncHttpTask movieTask = new AsyncHttpTask();
        movieTask.execute(fullPath);
        System.out.println("4444444444444444fullpath是" + fullPath);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (isOnline(getActivity())) {
            updateMovies();
        } else {
            Toast.makeText(getActivity(), "Network isn't available, check connection", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager mngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mngr.getActiveNetworkInfo();
        return !(info == null || (info.getState()) != NetworkInfo.State.CONNECTED);
    }

    public void prefChangeUpdate(SharedPreferences sharedPreferences, String key) {
        if (key.equals(R.string.pref_sort_key)) {
            updateMovies();
        }
    }

    //AsyncTask is used to spawn a background thread for the API call, keeping the main thread from being blocked.
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                mGridAdapter.setGridData(mMovie);
                mGridAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }

        }
    }

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        if (null != stream) {
            stream.close();
        }
        return result;
    }


    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("results");
            Movie movie;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                String poster = post.optString("poster_path");
                String fullPosterPath = "http://image.tmdb.org/t/p/w185/" + poster;
                String year = post.optString("release_date");
                String desc = post.optString("overview");
                String rating = post.optString("vote_average");
                String votes = post.optString("vote_count");
                String id = post.optString("id");


                movie = new Movie();
                movie.setTitle(title);
                movie.setYear(year);
                movie.setDesc(desc);
                movie.setRating(rating);
                movie.setImage(fullPosterPath);
                movie.setVotes(votes);
                movie.setId(id);

                mMovie.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void queryFavoriteMovies() {
        String columns[] = new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_KEY, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_POSTER_PATH,};
        Uri myUri = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cur = getActivity().managedQuery(myUri, columns, null, null, null);
        if (!mFavoriteMovie.isEmpty()) {
            mFavoriteMovie.clear();
        }

        if (cur.moveToFirst()) {
            String id = null;
            String title = null;
            String posterPath = null;
            do {
                id = cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_KEY));
                title = cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                posterPath = cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                //Toast.makeText(this, id + ” ” + userName, Toast.LENGTH_LONG).show();
                Movie movie = new Movie();
                movie.setId(id);
                movie.setTitle(title);
                movie.setImage(posterPath);
                mFavoriteMovie.add(movie);
            } while (cur.moveToNext());


        }
    }
}
