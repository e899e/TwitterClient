package com.kisaragilab.twitterclient.task;

import android.content.Context;
import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.exception.ApiCallLimitException;
import com.kisaragilab.twitterclient.helper.TwitterOauthHeaderGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class TwitterGetJSONArrayTask implements Callable<JSONArray> {

    private final String httpMethod;
    private final String urlString;
    private final Map<String, String> mRequestParams;

    private final TwitterOauthHeaderGenerator mGenerator;

    public TwitterGetJSONArrayTask(@NonNull Context context, String url) {
        mRequestParams = new HashMap<>();

        mRequestParams.put("count", "100");
        mRequestParams.put("truncated", "false");

        this.httpMethod = "GET";
        this.urlString = url;
        this.mGenerator = new TwitterOauthHeaderGenerator(context);
    }

    public void addReqParam(String key, String val) {
        mRequestParams.put(key, val);
    }

    @Override
    public JSONArray call() {
        try {
            URL url = new URL(concatURLandParams(urlString, mRequestParams));

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod(httpMethod);

            urlConn.setRequestProperty("Authorization", mGenerator.generateHeader(httpMethod, urlString, mRequestParams));

            urlConn.connect();

            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }

                System.out.println(output.toString());

                try {
                    return new JSONArray(output.toString());
                } catch(JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                InputStream in = urlConn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                System.out.println(output.toString());
                if(output.toString().contains("\"code\":88")) {
                    throw new ApiCallLimitException();
                } else {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    private String concatURLandParams(String url, Map<String, String> requestParams) {
        StringBuilder sb = new StringBuilder();
        if(requestParams.size() > 0) {
            requestParams.entrySet().forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));
            sb.deleteCharAt(sb.length() - 1);
            return url + "?" + sb.toString();
        } else {
            return url;
        }
    }

}
