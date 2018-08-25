package ml.medyas.popularmoviesapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utility {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 130);
        return noOfColumns;
    }
}