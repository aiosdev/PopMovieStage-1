package udacity.popmoviestage_2;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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


public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private TextView titleTextView;
    private ImageView imageView;
    private TextView yearTextView;
    private TextView descTextView;
    private TextView ratingBar;
    private TextView voteTextView;
    private ListView lvVideo;
    private ListView lvReview;

    private ArrayList<Review> mReview;
    private ArrayList<Video> mVideo;
    private ReviewAdapter mReviewAdapter;
    private VideoAdapter mVideoAdapter;

    private String mBase_URL = "http://api.themoviedb.org/3/movie/";
    private String mApi_key = "?api_key=" + BuildConfig.MOVIES_TMDB_API_KEY;
    private String mVideos = "videos";
    private String mReviews = "reviews";
    private String id = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String image = bundle.getString("image");
        String desc = bundle.getString("description");
        String year = bundle.getString("year");
        String rating = bundle.getString("rating");
        String votes = bundle.getString("votes");
        id = bundle.getString("id");

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);
        yearTextView = (TextView) findViewById(R.id.year);
        yearTextView.setText(year);
        descTextView = (TextView) findViewById(R.id.desc);
        descTextView.setText(desc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());
        ratingBar = (TextView) findViewById(R.id.ratingbar1);
        ratingBar.setText(rating + " / " + "10");
        voteTextView = (TextView) findViewById(R.id.votes);
        voteTextView.setText(votes + " votes");

        imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(image).placeholder(R.drawable.loader).into(imageView);

        lvReview = (ListView) findViewById(R.id.lv_review);
        mReview = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(this, R.layout.review_layout, mReview);
        lvReview.setAdapter(mReviewAdapter);

        //TODO

        lvVideo = (ListView) findViewById(R.id.lv_video);
        mVideo = new ArrayList<>();
        mVideoAdapter = new VideoAdapter(this, R.layout.video_layout, mVideo);
        lvVideo.setAdapter(mVideoAdapter);

        //TODO

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateReview();
        updateTrailer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTrailer() {
        String trailerPath = mBase_URL + id +"/"+ mVideos + mApi_key;
        AsyncTrailerTask trailerTask = new AsyncTrailerTask();
        trailerTask.execute(trailerPath);
    }

    private void updateReview() {
        String reviewPath = mBase_URL + id +"/"+ mReviews + mApi_key;
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


        private void parseTrailer(String result) {
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("results");
                Video trailer;
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    String trailerName = post.optString("name");
                    String key = post.optString("key");
                    int videoTotal = posts.length();

                    trailer = new Video();
                    trailer.setKey(key);
                    trailer.setTrailerName(trailerName);

                    mVideo.add(trailer);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    System.out.println("----------===========reponse"+response);
                    parseReview(response);


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


        private void parseReview(String result) {
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("results");
                Review review;
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    String author = post.optString("author");
                    String content = post.optString("content");
                    int reviewTotal = posts.length();

                    review = new Review();
                    review.setAuthor(author);
                    review.setReview(content);

                    mReview.add(review);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}