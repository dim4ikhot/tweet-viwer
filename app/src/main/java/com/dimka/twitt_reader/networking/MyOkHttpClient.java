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

    String baseUrl;

    public OkHttpClient getClient(final String accessToken, final String consumerSecret,
                                  final String accessSecret){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                String method = original.method();
                baseUrl = originalHttpUrl.toString();
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

                String params;
                if((!baseUrl.contains("image"))) {
                    params = builder.toString() + paramsOfBaseUrl;
                }
                else{
                    params = builder.toString();
                }

                params = sortParamsInAlphabeticOrder(baseUrl, params.replace(baseUrl+"?", ""));
                String signature = new SignatureGenerator()
                        .getSignature(method, baseUrl, consumerSecret,accessSecret, params);
                builder.addQueryParameter("oauth_signature", signature);

                HttpUrl url = builder.build();
                String finalUrl = baseUrl + "?" + sortParamsInAlphabeticOrder(baseUrl, url.toString() + paramsOfBaseUrl);

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(finalUrl);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        return httpClient.build();
    }

    /*
      s1.compareTo(s2)
      result:
      0 s1 = s2
      >0 s2 < s1
      <0 s2 > s1
    */

    public static String sortParamsInAlphabeticOrder(String baseUrl,String totalUrl){
        String sortedUrl = totalUrl.replace(baseUrl+"?", "");
        String[] ampersantSplited = sortedUrl.split("&");

        for(int i = 0; i < ampersantSplited.length; i++){
            for(int j = i+1; j < ampersantSplited.length; j++){
                String[] splitedI = ampersantSplited[i].split("=");
                String[] splitedJ = ampersantSplited[j].split("=");
                //it means that splitedJ[1] lexicographically less than splitedI[1]
                if(splitedI[0].compareTo(splitedJ[0]) > 0){
                    String temp = ampersantSplited[i];
                    ampersantSplited[i] = ampersantSplited[j];
                    ampersantSplited[j] = temp;
                }
            }
        }
        sortedUrl = "";
        for(String param: ampersantSplited){
            if(sortedUrl.equals("")) {
                sortedUrl += param;
            }
            else{
                sortedUrl += "&" + param;
            }
        }

        return sortedUrl;
    }
}
