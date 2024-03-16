package com.kisaragilab.twitterclient.task;

import android.content.Context;
import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.helper.TwitterOauthHeaderGenerator;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import static com.twitter.sdk.android.core.internal.network.UrlUtils.percentEncode;

public class TwitterPostTask implements Callable<Exception> {

    private final String httpMethod;
    private final String header;
    private final String urlString;

    public TwitterPostTask(@NonNull Context context, String url, Map<String, String> requestParams) {
        TwitterOauthHeaderGenerator generator = new TwitterOauthHeaderGenerator(context);
        this.httpMethod = "POST";
        this.urlString = concatURLandParams(url, requestParams);
        this.header = generator.generateHeader(httpMethod, url, requestParams);
    }

    @Override
    public Exception call() {
        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod(httpMethod);
            urlConn.setRequestProperty("Authorization", header);

            urlConn.connect();

            int status = urlConn.getResponseCode();
            System.out.println(urlConn.getResponseMessage());

            if (status == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                System.out.println(output.toString());
            } else {
                InputStream in = urlConn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                System.out.println(output.toString());
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }

        return null;
    }

    private String concatURLandParams(String url, Map<String, String> requestParams) {
        StringBuilder sb = new StringBuilder();
        if(requestParams.size() > 0) {
            requestParams.entrySet().forEach(entry -> sb.append(entry.getKey()).append("=").append(percentEncode(entry.getValue())).append("&"));
            sb.deleteCharAt(sb.length() - 1);
            return url + "?" + sb.toString();
        } else {
            return url;
        }
    }

}
