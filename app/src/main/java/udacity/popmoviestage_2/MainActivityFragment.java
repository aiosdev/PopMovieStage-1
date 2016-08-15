package udacity.popmoviestage_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.support.v4.app.Fragment;

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
import java.util.zip.Inflater;

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
        Inflater.inflate(R.menu.menu_grid_view, menu);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Inflater.inflate(R.menu.menu_grid_view, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);

        mMovie = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.fragment_main, mMovie);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                intent.putExtra("title", movie.getTitle()).
                        putExtra("image", movie.getImage()).
                        putExtra("year", movie.getYear()).
                        putExtra("description", movie.getDesc()).
                        putExtra("rating", movie.getRating()).
                        putExtra("votes", movie.getVotes()).
                        putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        return view;
    }


    public void updateMovies() {
        mGridAdapter.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mSort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        String fullPath = mBase_URL + mSort + mApi_key;
        AsyncHttpTask movieTask = new AsyncHttpTask();
        movieTask.execute(fullPath);
        System.out.println("4444444444444444fullpath是" + fullPath);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (isOnline(this)) {
            updateMovies();
        } else {
            Toast.makeText(MainActivityFragment.this, "Network isn't available, check connection", Toast.LENGTH_LONG).show();
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
            } else {
                Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
