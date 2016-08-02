/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package udacity.popmoviestage_2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "udacity.popmoviestage_2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_POPULARITY_PAGE_NUMBER = "popularity_page_number";
        public static final String COLUMN_RATING_PAGE_NUMBER = "rating_page_number";


        public static final int COL_INDEX_MOVIE_ID = 0;
        public static final int COL_INDEX_MOVIE_KEY = 1;
        public static final int COL_INDEX_POSTER_PATH = 2;
        public static final int COL_INDEX_BACKDROP_PATH = 3;
        public static final int COL_INDEX_TRAILER_PATH = 4;
        public static final int COL_INDEX_ADULT = 5;
        public static final int COL_INDEX_TITLE = 6;
        public static final int COL_INDEX_OVERVIEW = 7;
        public static final int COL_INDEX_RELEASE_DATE = 8;
        public static final int COL_INDEX_VOTE_AVERAGE = 9;
        public static final int COL_INDEX_POPULARITY = 10;
        public static final int COL_INDEX_FAVORITE = 11;
        public static final int COL_INDEX_POPULARITY_PAGE_NUMBER = 12;
        public static final int COL_INDEX_RATING_PAGE_NUMBER = 13;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TrailerEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_TRAILER).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_TRAILER;

        /*Table */
        public static final String TABLE_NAME = "trailer";

        /* Columns */
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_TRAILER_KEY = "trailer_id";
        public static final String COLUMN_TRAILER_URL = "trailer_url";
        public static final String COLUMN_TRAILER_IMAGE_URL = "trailer_image_url";

        public static final int COL_INDEX_ID = 0;
        public static final int COL_INDEX_MOVIE_KEY = 1;
        public static final int COL_INDEX_TRAILER_KEY = 2;
        public static final int COL_INDEX_TRAILER_URL = 3;
        public static final int COL_INDEX_TRAILER_IMAGE_URL = 4;

        public static Uri buildTrailerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri builerTrailerUriForMovie(int movieKey){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieKey)).build();
        }

        public static String getMovieFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ReviewEntry implements BaseColumns{
        /* Content Provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_REVIEW;

        /* Table */
        public static final String TABLE_NAME = "review";

        /*Columns */
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_REVIEW_KEY = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";


        public static final int COL_INDEX_ID = 0;
        public static final int COL_INDEX_MOVIE_KEY = 1;
        public static final int COL_INDEX_REVIEW_KEY = 2;
        public static final int COL_INDEX_AUTHOR = 3;
        public static final int COL_INDEX_CONTENT = 4;

        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildReviewUriForMovie(int movieKey){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieKey)).build();
        }

        /* Getters */
        public static String getMovieFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

}
