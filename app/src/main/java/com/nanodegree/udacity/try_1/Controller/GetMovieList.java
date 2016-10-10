package com.nanodegree.udacity.try_1.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.udacity.try_1.model.JSONParse;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jay on 06/10/2016.
 */

public class GetMovieList extends AsyncTask<String, Void, Void>{

    private final String LOG_TAG = getClass().getSimpleName();
    private final String API_KEY = "b4f671eb8c6786d9b16338a9de5e6898";

    private Context context;

    public GetMovieList(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        Log.d(LOG_TAG, "bg process started");
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;

        try{
            final String BASE_URL = "http://api.themoviedb.org/3/movie/"+strings[0]+"?api_key="+API_KEY;

            URL url = new URL(BASE_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if(inputStream == null){
                Log.d(LOG_TAG, "Input Stream Empty");
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer buffer = new StringBuffer();
            String line;
            while((line = bufferedReader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                Log.d(LOG_TAG,"String Buffer Empty");
                return null;
            }

            movieJsonStr = buffer.toString();

            JSONParse jsonParse = new JSONParse(context, movieJsonStr);
            jsonParse.storeDataToDB();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
