package com.kisaragilab.twitterclient.task;

import android.content.Context;
import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.exception.ApiCallLimitException;
import com.kisaragilab.twitterclient.helper.TwitterOauthHeaderGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class TwitterGetJSONObjectTask implements Callable<JSONObject> {

    protected final String httpMethod;
    protected final String baseUrl;
    private final Map<String, String> requestParams;
    private final TwitterOauthHeaderGenerator generator;

    public TwitterGetJSONObjectTask(@NonNull Context context, String baseUrl) {
        this.httpMethod = "GET";
        this.baseUrl = baseUrl;
        this.requestParams = new HashMap<>();
        this.generator = new TwitterOauthHeaderGenerator(context);
    }

    public void addReqParam(String key, String val) {
        requestParams.put(key, val);
    }

    public void addReqParam(String reqString) {
        String[] reqGroups = reqString.split("&");
        for(String reqGroup : reqGroups) {
            String[] req = reqGroup.split("=");
            requestParams.put(req[0], req[1]);
        }
    }

    @Override
    public JSONObject call() {
        try {
            URL url = new URL(concatURLandParams(baseUrl, requestParams));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(httpMethod);

            connection.setRequestProperty("Authorization", generator.generateHeader(httpMethod, baseUrl, requestParams));

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }

                try {
                    return new JSONObject(output.toString());
                } catch(JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                InputStream in = connection.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder output = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }

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
