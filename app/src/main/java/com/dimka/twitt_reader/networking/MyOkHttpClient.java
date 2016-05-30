package com.dimka.twitt_reader.networking;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Created by mityai on 26.05.2016.
 */
public class MyOkHttpClient {

    public OkHttpClient getClient(final String accessToken, final String consumerSecret,
                                  final String accessSecret){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                String method = original.method();
                String baseUrl = originalHttpUrl.toString();
                String paramsOfBaseUrl = "";

                if(baseUrl.contains("?")) {
                    paramsOfBaseUrl = "&" + baseUrl.substring(baseUrl.indexOf("?")+1, baseUrl.length());
                    baseUrl = baseUrl.substring(0,baseUrl.indexOf("?"));
                }

                HttpUrl.Builder builder = originalHttpUrl.newBuilder(baseUrl)
                        .addQueryParameter("oauth_consumer_key", "fRuPxDVC1J58r05jdJIDn7qCJ")
                        .addQueryParameter("oauth_nonce", ""+(int) (Math.random() * 100000000))
                        .addQueryParameter("oauth_signature_method", "HMAC-SHA1")
                        .addQueryParameter("oauth_timestamp", "" + (System.currentTimeMillis() /1000))
                        .addQueryParameter("oauth_token", accessToken)
                        .addQueryParameter("oauth_version", "1.0");

                String params = builder.toString() + paramsOfBaseUrl;
                params = params.replace(baseUrl+"?", "");
                String signature = new SignatureGenerator()
                        .getSignature(method, baseUrl, consumerSecret,accessSecret, params);
                builder.addQueryParameter("oauth_signature", signature);

                HttpUrl url = builder.build();
                String finalUrl = url.toString() + paramsOfBaseUrl;
                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(finalUrl);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        return httpClient.build();
    }
}
