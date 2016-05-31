package com.muchev.risto.hnrr.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Risto on 5/31/2016.
 */
public class DownloadHelper {

    private static final String TAG = DownloadHelper.class.getSimpleName();

    public static String downloadRss(String urlPath) {
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            int response = connection.getResponseCode();
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int charRead;
            char[] charBuffer = new char[256];

            while ((charRead = isr.read(charBuffer, 0, charBuffer.length))!=-1) {
                buffer.append(String.valueOf(charBuffer, 0, charRead));
            }

            return buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "IO exception" + e.getMessage());
        }

        return null;
    }

}
