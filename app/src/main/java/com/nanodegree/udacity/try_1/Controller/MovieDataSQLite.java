package com.nanodegree.udacity.try_1.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nanodegree.udacity.try_1.model.MovieSortPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jay on 07/10/2016.
 */

public class MovieDataSQLite extends SQLiteOpenHelper{

    private final String LOG_TAG = getClass().getSimpleName();

    private static final String DB_NAME = "MovieData.db";
    private Context context = null;

    private static final String MOVIE_LIST_TABLE = "MoviesList";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MOVIE_ID = "movie_id";
    private static final String COLUMN_MOVIE_NAME = "movie_name";
    private static final String COLUMN_POSTER_PATH_LOCAL = "poster_path_local";
    private static final String COLUMN_ADULT_BOOL = "adult";
    private static final String COLUMN_MOVIE_OVERVIEW = "overview";
    private static final String COLUMN_RELEASE_DATE = "release_date";
    private static final String COLUMN_GENRE_IDS = "genre_ids";
    private static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
    private static final String COLUMN_BACKDROP_PATH_LOCAL = "backdrop_path_local";
    private static final String COLUMN_POPULARITY = "popularity";
    private static final String COLUMN_VOTE_COUNT = "vote_count";
    private static final String COLUMN_VIDEO = "video";
    private static final String COLUMN_VOTE_AVERAGE = "vote_average";
    private static final String COLUMN_FAVOURITE_BOOL = "favourite";


    public MovieDataSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MovieDataSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public MovieDataSQLite(Context context){
        super(context, DB_NAME, null, 1);

        this.context = context;
        Log.d(LOG_TAG, "object created SQL");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + MOVIE_LIST_TABLE + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_MOVIE_ID + " integer not null," +
                COLUMN_MOVIE_NAME + " text," +
                COLUMN_POSTER_PATH_LOCAL + " blob default 'null'," +
                COLUMN_ADULT_BOOL + " text," +
                COLUMN_MOVIE_OVERVIEW + " text," +
                COLUMN_RELEASE_DATE + " text," +
                COLUMN_GENRE_IDS + " text default 'null'," +
                COLUMN_ORIGINAL_LANGUAGE + " text," +
                COLUMN_BACKDROP_PATH_LOCAL + " blob default 'null'," +
                COLUMN_POPULARITY + " real," +
                COLUMN_VOTE_COUNT + " real," +
                COLUMN_VOTE_AVERAGE + " real," +
                COLUMN_FAVOURITE_BOOL + " integer" +
        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + MOVIE_LIST_TABLE);
        this.onCreate(sqLiteDatabase);
    }

    public boolean insertMovieData(int movie_id, String movie_name, String poster_absolute_path, String adult, String overview, String release_date, String lang, double popularity, double vote_count, double vote_avg, String favourite){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MOVIE_ID, movie_id);
        cv.put(COLUMN_MOVIE_NAME, movie_name);
        cv.put(COLUMN_POSTER_PATH_LOCAL, poster_absolute_path);
        cv.put(COLUMN_ADULT_BOOL, adult);
        cv.put(COLUMN_MOVIE_OVERVIEW, overview);
        cv.put(COLUMN_RELEASE_DATE, release_date);
        cv.put(COLUMN_ORIGINAL_LANGUAGE, lang);
        cv.put(COLUMN_POPULARITY, popularity);
        cv.put(COLUMN_VOTE_COUNT, vote_count);
        cv.put(COLUMN_VOTE_AVERAGE, vote_avg);
        cv.put(COLUMN_FAVOURITE_BOOL, favourite);

        long id = db.insertOrThrow(MOVIE_LIST_TABLE, COLUMN_GENRE_IDS + "," + COLUMN_BACKDROP_PATH_LOCAL, cv);

        if(id < 0)
            return false;

        return true;
    }

    public HashMap<String, String> fetchAllMoviesList(MovieSortPreference sortPreference) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String sortPref = sortPreference.readPrefence();
        if(sortPref.equalsIgnoreCase("favourite")){
            cursor = db.rawQuery("select " + COLUMN_ID +", " + COLUMN_MOVIE_ID + ", " + COLUMN_POSTER_PATH_LOCAL + " from " + MOVIE_LIST_TABLE + " where " + COLUMN_FAVOURITE_BOOL + " = 1", null);
        }
        else if(sortPref.equalsIgnoreCase("top_rated")){
            cursor = db.rawQuery("select " + COLUMN_ID +", " + COLUMN_MOVIE_ID + ", " + COLUMN_POSTER_PATH_LOCAL + " from " + MOVIE_LIST_TABLE + " order by " + COLUMN_VOTE_AVERAGE + " desc", null);
        }
        else if(sortPref.equalsIgnoreCase("popular")){
            cursor = db.rawQuery("select " + COLUMN_ID +", " + COLUMN_MOVIE_ID + ", " + COLUMN_POSTER_PATH_LOCAL + " from " + MOVIE_LIST_TABLE + " order by " + COLUMN_POPULARITY + " desc", null);
        }

//        cursor = db.rawQuery("select " + COLUMN_ID +", " + COLUMN_MOVIE_ID + ", " + COLUMN_POSTER_PATH_LOCAL + " from " + MOVIE_LIST_TABLE, null);

        HashMap<String, String> movieListHashMap = new HashMap<>();

        cursor.moveToFirst();
        while(cursor.moveToNext()){
            movieListHashMap.put(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTER_PATH_LOCAL))
            );
        }

        return movieListHashMap;
    }

    public JSONObject fetchMovieDetails(String movie_id) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MOVIE_LIST_TABLE + " where movie_id = ?", new String[]{movie_id});

        String jsonStr = null;
        cursor.moveToFirst();
        int i =0;

        Log.d(LOG_TAG, String.valueOf(i));
        jsonStr = "{";

            jsonStr += "movie_id : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_ID)) + ",";
            jsonStr += " name : '" + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_NAME)).replace("'", "") + "',";
            jsonStr += " poster_path_local : '" + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POSTER_PATH_LOCAL)) + "',";
            jsonStr += " adult_bool : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADULT_BOOL)) + ",";
            jsonStr += " overview : '" + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOVIE_OVERVIEW)).replace("'", "") + "',";
            jsonStr += " release_date : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RELEASE_DATE)) + ",";
            jsonStr += " lang : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORIGINAL_LANGUAGE)) + ",";
            jsonStr += " popularity : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POPULARITY)) + ",";
            jsonStr += " vote_count : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOTE_COUNT)) + ",";
            jsonStr += " vote_average : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOTE_AVERAGE)) + ",";
            jsonStr += " favourite : " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAVOURITE_BOOL));

        jsonStr += "}";

        Log.d(LOG_TAG, jsonStr);

        JSONObject jsonObject = new JSONObject(jsonStr);

        return jsonObject;
    }

    public void addMovieToFavourites(String movie_id){
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("update " + MOVIE_LIST_TABLE + " set " + COLUMN_FAVOURITE_BOOL + " = 1 where movie_id = ?", movie_id);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FAVOURITE_BOOL, 1);
        int res = db.update(MOVIE_LIST_TABLE, cv, COLUMN_MOVIE_ID + " = ?", new String[]{movie_id});

        Log.d(LOG_TAG + " affected rows ", String.valueOf(res));
    }
}
