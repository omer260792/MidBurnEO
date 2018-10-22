package com.example.omer.midburneo.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

public class UtilHelper extends AppCompatActivity {

    private static final String TAG = "UtilHelper";

    private Context context;



    public static String getStaticTimeInString(String time) {

        long currentDateTime = System.currentTimeMillis();
        String StringCurrentMil = String.valueOf(time);

        DateFormat getTimeHourMintus = new SimpleDateFormat("HH:mm");

        time = getTimeHourMintus.format(currentDateTime);
        return time;
    }



    public static File getTempFile(Context context, String url) {
        File file = null;
        String fileName;
        try {
            fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
            File directory = context.getFilesDir();
            File fileDe = new File(directory, fileName);
            Log.e("fff", String.valueOf(fileDe));
            Log.e("fff", String.valueOf(directory));

        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    public static File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public static boolean checkInternetConnection(Context context){

        Boolean isConnected;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


        return isConnected;

    }


}
