package com.nanodegree.udacity.try_1.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jay on 08/10/2016.
 */

public class ImageProcessing {

    private final String LOG_TAG = getClass().getSimpleName();
    private Context context;

    public ImageProcessing(Context context){
        this.context = context;
    }

    public String savePosterToInternalStorage(Bitmap bitmap, String imageName){
        ContextWrapper cw = new ContextWrapper(context);

        // Create or open dir POSTERS
        File dir = cw.getDir("posters", Context.MODE_PRIVATE);
        File myPath = new File(dir, imageName);

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try{
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dir.getAbsolutePath();
    }

    public Bitmap fetchPosterFromInternalStorage(String dirPath, String imageName, String extension){
        try{
            Log.d(LOG_TAG, dirPath + imageName + "." + extension);
            File file = new File(dirPath, imageName + "." + extension);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            return bitmap;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
