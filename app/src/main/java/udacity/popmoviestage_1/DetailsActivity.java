package udacity.popmoviestage_1;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private ImageView imageView;
    private TextView yearTextView;
    private TextView runtimeTextView;
    private TextView descTextView;
    private TextView ratingBar;
    private TextView voteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

//        final RatingBar ratingBar1 = (RatingBar) findViewById(R.id.ratingbar1);

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String image = bundle.getString("image");
        String desc = bundle.getString("description");
        String year = bundle.getString("year");
        String rating = bundle.getString("rating");
        String votes = bundle.getString("votes");

//        float fRating = Float.parseFloat(rating);
//        ratingBar1.setRating(fRating / 2);

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);
        yearTextView = (TextView) findViewById(R.id.year);
        yearTextView.setText(year);
        descTextView = (TextView) findViewById(R.id.desc);
        descTextView.setText(desc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());
        ratingBar = (TextView) findViewById(R.id.ratingbar1);
        ratingBar.setText(rating + "/" + "10");
        voteTextView = (TextView) findViewById(R.id.votes);
        voteTextView.setText(votes + "votes");

        imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(image).into(imageView);
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


}