package com.nanodegree.udacity.try_1.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jay on 10/10/2016.
 */

public class MovieSortPreference {

    private Context context;
    private SharedPreferences preferences;

    public MovieSortPreference(Context context){
        this.context = context;
        preferences = context.getSharedPreferences("sort_pref", Context.MODE_PRIVATE);
    }

    public void editPreference(String pref_value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sort", pref_value);
        editor.commit();
    }

    public String readPrefence(){
        return preferences.getString("sort", "popular");

    }

}
