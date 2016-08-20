package udacity.popmoviestage_2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class DetailsActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

    private TextView titleTextView;
    private ImageView imageView;
    private TextView yearTextView;
    private TextView descTextView;
    private TextView ratingBar;
    private TextView voteTextView;
    private ListView lvVideo;
    private ListView lvReview;
    private Button favoriteButton;

    private ArrayList<Review> mReview;
    private ArrayList<Video> mVideo;
    private ReviewAdapter mReviewAdapter;
    private VideoAdapter mVideoAdapter;

    private String mBase_URL = "http://api.themoviedb.org/3/movie/";
    private String mApi_key = "?api_key=" + BuildConfig.MOVIES_TMDB_API_KEY;
    private String mVideos = "videos";
    private String mReviews = "reviews";

    private String key = null;

    private String id = null;
    private String title = null;
    private String image = null;
    private String desc = null;
    private String year = null;
    private String rating = null;
    private String votes = null;

    private ScrollView mDetailLayout;

    public static final String TAG = DetailsActivityFragment.class.getSimpleName();
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    private Movie mMovie;

    public DetailsActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateReview();
        //updateTrailer();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(DetailsActivityFragment.DETAIL_MOVIE);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDetailLayout = (ScrollView) rootView.findViewById(R.id.sv_detail_layout);

        if (mMovie != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        if (mMovie != null) {
            titleTextView = (TextView) rootView.findViewById(R.id.title);
            titleTextView.setText(mMovie.getTitle());
            yearTextView = (TextView) rootView.findViewById(R.id.year);
            yearTextView.setText(mMovie.getYear());
            descTextView = (TextView) rootView.findViewById(R.id.desc);
            descTextView.setText(mMovie.getDesc());
            descTextView.setMovementMethod(new ScrollingMovementMethod());
            ratingBar = (TextView) rootView.findViewById(R.id.ratingbar1);
            ratingBar.setText(mMovie.getRating() + " / " + "10");
            voteTextView = (TextView) rootView.findViewById(R.id.votes);
            voteTextView.setText(mMovie.getVotes() + " votes");

            favoriteButton = (Button) rootView.findViewById(R.id.bt_favorite);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY,mMovie.getId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getImage());
                    getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                    Toast.makeText(getContext(), "Marked as favorite", Toast.LENGTH_LONG).show();
                }
            });

            imageView = (ImageView) rootView.findViewById(R.id.imageView);
            Picasso.with(getActivity()).load(mMovie.getImage()).placeholder(R.drawable.loader).into(imageView);

            mReview = new ArrayList<>();
           lvReview = (ListView) rootView.findViewById(R.id.lv_review);
            mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.review_layout, mReview);
            lvReview.setAdapter(mReviewAdapter);
            updateReview();


            lvVideo = (ListView) rootView.findViewById(R.id.lv_video);
            mVideo = new ArrayList<>();
            mVideoAdapter = new VideoAdapter(getActivity(), R.layout.video_layout, mVideo);
            lvVideo.setAdapter(mVideoAdapter);
            updateTrailer();

            lvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                        startActivity(intent);
                        Toast.makeText(getContext(), "Being launched by YouTube", Toast.LENGTH_LONG).show();
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + key));
                        startActivity(intent);
                    }
                }
            });
        }
        return rootView;
    }

    private void updateTrailer() {
        String trailerPath = mBase_URL + id + "/" + mVideos + mApi_key;
        AsyncTrailerTask trailerTask = new AsyncTrailerTask();
        trailerTask.execute(trailerPath);
        System.out.println("============--------------=======path" + trailerPath);

    }

    private void updateReview() {
        String reviewPath = mBase_URL + id + "/" + mReviews + mApi_key;
        AsyncReviewTask reviewTask = new AsyncReviewTask();
        reviewTask.execute(reviewPath);
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

    public class AsyncTrailerTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(strings[0]));
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseTrailer(response);
                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
//                Log.d(LOG_TAG,""+ e.getLocalizedMessage());
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mVideoAdapter.setVideo(mVideo);
        }

    }

    private void parseTrailer(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("results");
            System.out.println("---------------------reponse是" + response.toString());
            Video trailer;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String trailerName = post.optString("name");
                key = post.optString("key");

                trailer = new Video();
                trailer.setKey(key);
                trailer.setTrailerName(trailerName);

                mVideo.add(trailer);
                System.out.println("===---===----===---mVideo是" + mVideo.size());
                System.out.println("===---===----===---trailer是" + trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class AsyncReviewTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = 0;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(strings[0]));
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    System.out.println("----------===========reponse" + response);
                    parseReview(response);
                    System.out.println("```````````==========mReview大小是" + mReview.size());

                    result = 1;
                } else {
                    result = 0;
                }
            } catch (Exception e) {
//                Log.d(LOG_TAG,"" + e.getLocalizedMessage());
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mReviewAdapter.setReview(mReview);
        }
    }

    private void parseReview(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("results");
            Review review;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String author = post.optString("author");
                String content = post.optString("content");
                System.out.println("--===---===---===内容是" + content);


                review = new Review();
                review.setAuthor(author);
                review.setReview(content);

                mReview.add(review);
                System.out.println("--------------==============review是 " + mReview.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void setReviewListViewHeightBasedOnChildren(ListView lvReview) {
//
//        ReviewAdapter mReviewAdapter = (ReviewAdapter) lvReview.getAdapter();
//        if (mReviewAdapter == null) {
//            return;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0; i < mReviewAdapter.getCount(); i++) {
//            View listItem = mReviewAdapter.getView(i, null, lvReview);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//
//        ViewGroup.LayoutParams params = lvReview.getLayoutParams();
//        params.height = totalHeight
//                + (lvReview.getDividerHeight() * (mReviewAdapter.getCount() - 1));
//        lvReview.setLayoutParams(params);
//    }

    private void toggleMovieFavorite() {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "= '" + id.trim() + "'";
        if (queryFM(selection)) {
            Toast.makeText(getActivity(), "This movie is already in favirate folder !", Toast.LENGTH_LONG).show();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, image);
            System.out.println("MovieContract.MovieEntry.COLUMN_TITLE" + title);
            System.out.println("MovieContract.MovieEntry.COLUMN_MOVIE_KEY" + id);
            getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            //String stringUri = MovieContract.BASE_CONTENT_URI.toString() + "/movie";
            //Uri uri = Uri.parse(stringUri);

            //contentValues.put(MovieContract.MovieEntry.COLUMN_);
        }

    }

    private boolean queryFM(String selection) {
        String u = "difiefjie";
        String columns[] = new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_KEY, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_POSTER_PATH};
        Uri myUri = MovieContract.MovieEntry.CONTENT_URI;
        //String selection = "123456";//MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "= '" + movieId.trim() + "'";
        Cursor cur = getActivity().managedQuery(myUri, columns, selection, null, null);
        if (cur.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }
}