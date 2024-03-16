package com.kisaragilab.twitterclient.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class ImageDownloadTask implements Callable<Bitmap> {

    private final String urlString;

    public ImageDownloadTask(@NonNull String url) {
        if(!url.contains("https")) {
            this.urlString = url.replace("http", "https");
        } else {
            this.urlString = url;
        }
    }

    @Override
    public Bitmap call() {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("GET");

            urlConn.connect();

            int status = urlConn.getResponseCode();
            System.out.println(urlConn.getResponseMessage());

            if (status == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConn.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

}
