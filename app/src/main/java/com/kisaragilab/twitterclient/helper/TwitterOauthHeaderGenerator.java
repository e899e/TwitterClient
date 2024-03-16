package com.kisaragilab.twitterclient.helper;

import android.content.Context;
import androidx.annotation.NonNull;
import com.kisaragilab.twitterclient.R;
import com.kisaragilab.twitterclient.util.SharedPreferenceManager;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import static com.twitter.sdk.android.core.internal.network.UrlUtils.percentEncode;

public class TwitterOauthHeaderGenerator {

    private final String consumerKey;
    private final String consumerSecret;
    private final String signatureMethod;
    private final String token;
    private final String tokenSecret;
    private final String version;

    public TwitterOauthHeaderGenerator(@NonNull Context context) {
        this.consumerKey = context.getString(R.string.api_key);
        this.consumerSecret = context.getString(R.string.api_key_secret);

        SharedPreferenceManager spm = new SharedPreferenceManager(context);
        this.token = spm.loadUserAccessToken();
        this.tokenSecret = spm.loadUserAccessTokenSecret();
        this.signatureMethod = "HMAC-SHA1";
        this.version = "1.0";
    }

    private static final String oauth_consumer_key = "oauth_consumer_key";
    private static final String OAUTH_TOKEN = "oauth_token";
    private static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    private static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    private static final String OAUTH_NONCE = "oauth_nonce";
    private static final String OAUTH_VERSION = "oauth_version";
    private static final String OAUTH_SIGNATURE = "oauth_signature";
    private static final String HMAC_SHA1 = "HmacSHA1";

    public String generateHeader(String httpMethod, String url, Map<String, String> requestParams) {
        StringBuilder base = new StringBuilder();
        String nonce = getNonce();
        String timestamp = getTimestamp();
        String baseSignatureString = generateSignatureBaseString(httpMethod, url, requestParams, nonce, timestamp);
        String signature = encryptUsingHmacSHA1(baseSignatureString);
        base.append("OAuth ");
        append(base, oauth_consumer_key, consumerKey);
        append(base, OAUTH_NONCE, nonce);
        append(base, OAUTH_SIGNATURE, signature);
        append(base, OAUTH_SIGNATURE_METHOD, signatureMethod);
        append(base, OAUTH_TIMESTAMP, timestamp);
        append(base, OAUTH_TOKEN, token);
        append(base, OAUTH_VERSION, version);
        base.deleteCharAt(base.length() - 1);
        return base.toString();
    }

    private String generateSignatureBaseString(String httpMethod, String url, Map<String, String> requestParams, String nonce, String timestamp) {
        Map<String, String> params = new HashMap<>();
        requestParams.entrySet().forEach(entry -> put(params, entry.getKey(), entry.getValue()));
        put(params, oauth_consumer_key, consumerKey);
        put(params, OAUTH_NONCE, nonce);
        put(params, OAUTH_SIGNATURE_METHOD, signatureMethod);
        put(params, OAUTH_TIMESTAMP, timestamp);
        put(params, OAUTH_TOKEN, token);
        put(params, OAUTH_VERSION, version);
        Map<String, String> sortedParams = params.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        StringBuilder base = new StringBuilder();
        sortedParams.entrySet().forEach(entry -> base.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));
        base.deleteCharAt(base.length() - 1);
        return httpMethod.toUpperCase() + "&" + percentEncode(url) + "&" + percentEncode(base.toString());
    }

    private String encryptUsingHmacSHA1(String input) {
        String secret = percentEncode(consumerSecret) + "&" + percentEncode(tokenSecret);
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);
        Mac mac;
        try {
            mac = Mac.getInstance(HMAC_SHA1);
            mac.init(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
        byte[] signatureBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(signatureBytes));
    }

    private void put(Map<String, String> map, String key, String value) {
        if(!value.contains(":")) {
            map.put(percentEncode(key), percentEncode(value));
        } else {
            map.put(percentEncode(key), value);
        }
    }

    private void append(StringBuilder builder, String key, String value) {
        builder.append(percentEncode(key)).append("=\"").append(percentEncode(value)).append("\",");
    }

    private String getNonce() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

    }

    private String getTimestamp() {
        return Math.round((new Date()).getTime() / 1000.0) + "";
    }

}
