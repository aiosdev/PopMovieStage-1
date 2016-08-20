package udacity.popmoviestage_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private boolean mTwoPane;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailsActivityFragment(),
                                DetailsActivityFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsActivityFragment.DETAIL_MOVIE, movie);

            DetailsActivityFragment fragment = new DetailsActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailsActivityFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra(DetailsActivityFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }

}
