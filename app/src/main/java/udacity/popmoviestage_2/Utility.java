package udacity.popmoviestage_2;

import android.content.Context;
import android.database.Cursor;

import udacity.popmoviestage_2.data.MovieContract;

/**
 * Created by guoecho on 2016/7/15.
 */
public class Utility {

    public static int isFavorited(Context context, int id){
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{Integer.toString(id)},
                null
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String builderImageUrl(int width, String fileName){
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }
}
