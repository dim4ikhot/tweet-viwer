package com.dimka.twitt_reader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;

import com.dimka.twitt_reader.list_adapters.TweetsViewAdapter;
import com.dimka.twitt_reader.networking.MyOkHttpClient;
import com.dimka.twitt_reader.networking.MyRetrofitBuilder;
import com.dimka.twitt_reader.pojo_classes.account_settings.AccauntSettings;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.verify_credentials.VerifyCredentials;
import com.dimka.twitt_reader.rest_api_retrofit_interface.TwitterRest;
import com.dimka.twitt_reader.tweet.NewTweetActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    static String CALLBACK_URL = "x-oauthflow-twitter://twitterlogin";
    public static String ACCESS_TOKEN_KEY = "access_token_key";
    public static String ACCESS_SECRET_KEY = "access_token_secret";
    public static String IS_AUTHORIZED_KEY = "is_authorized";
    public static int NEW_TWEET = 0;
    public static int VIEW_PROFILE = 1;

    static String verifier;
    SharedPreferences preferences;

    static Twitter twitter;
    static RequestToken requestToken;
    static AccessToken accessToken;
    String consumerKey;
    String consumerSecret;
    AccauntSettings accauntSettings;
    ListView homeTimeline;

    MenuItem profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeTimeline = (ListView)(findViewById(R.id.all_tweets));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        consumerKey = getResources().getString(R.string.consumer_key);
        consumerSecret = getResources().getString(R.string.consumer_secret);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getAccessToken();

        //This button serves for test any requests
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String correct = MyOkHttpClient.sortParamsInAlphabeticOrder("https://api.twitter.com/1.1/statuses/home_timeline.json",
                            "https://api.twitter.com/1.1/statuses/home_timeline.json?oauth_version=1%2E0&oauth_consumer_key=fRuPxDVC1J58r05jdJIDn7qCJ&oauth_nonce=fc7a26742d8b307f3a94d98c498a56d8&oauth_signature=oek0fJOkahQzkzLp2lbkwLGu4tg%3D&oauth_signature_method=HMAC%2DSHA1&oauth_timestamp=1464203403&oauth_token=3996686661%2DZrhwq55zZpMV83YudVke38hm8oeECSNXrwwsati&count=5&user_id=333");
                }
            });
        }
    }

    public static class getAccauntInfo extends AsyncTask<Void,Void,String>{

        Context context;
        Bitmap bmp = null;
        List<CommonStatusClass> homeStatuses = new ArrayList<>();

        public getAccauntInfo(Context ctx){
            context = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            Call<AccauntSettings> result = Internet.service.accauntSettings();
            Call<VerifyCredentials> verifyCredentialsCall = Internet.service.getVerifyCredentials();
            Call<List<CommonStatusClass>> callHomeStatuses =
                    Internet.service.getHomeTimeline(5);
            String profileImage = "";
            try {
                Internet.verifyCredentials = verifyCredentialsCall.execute().body();
                profileImage = Internet.verifyCredentials.getProfileImageUrlHttps();
                homeStatuses = callHomeStatuses.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(!profileImage.equals("")) {
                try {
                    bmp = Picasso.with(context).load(profileImage).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Internet.verifyCredentials.getName();
        }
        @Override
        protected void onPostExecute(String result){
            String title = Internet.verifyCredentials.getName().length() > 20?
                    Internet.verifyCredentials.getName().substring(0,19) + "...":
                    Internet.verifyCredentials.getName();
            ((MainActivity)context).profile.setTitle(title);
            if(bmp != null) {
                BitmapDrawable bmpDrawable = new BitmapDrawable(context.getResources(), bmp);
                ((MainActivity)context).profile.setIcon(bmpDrawable);
            }
            if(homeStatuses != null){
                TweetsViewAdapter adapter = new TweetsViewAdapter(context, homeStatuses);
                ((MainActivity)context).homeTimeline.setAdapter(adapter);
            }
            //Toast.makeText(context,"Welcome " + result,Toast.LENGTH_LONG).show();
        }
    }

    private void getAccessToken(){
        new Authorization().execute((Void) null);
    }

    public boolean checkIsAuthorized(){
        return preferences.getBoolean(IS_AUTHORIZED_KEY, false);
    }

    public void setKeysAfterSuccessAuthorised(String accessToken, String tokenSecret, boolean isauthorized){
        SharedPreferences.Editor e = preferences.edit();
        e.putString(ACCESS_TOKEN_KEY, accessToken);
        e.putString(ACCESS_SECRET_KEY, tokenSecret);
        e.putBoolean(IS_AUTHORIZED_KEY, isauthorized);
        e.apply();
    }

    public class Authorization extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            String urlString = "";
            if(!checkIsAuthorized()) {
                twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(consumerKey, consumerSecret);
                try {
                    requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
                    urlString = requestToken.getAuthorizationURL();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                String accessToken_ = preferences.getString(ACCESS_TOKEN_KEY,"");
                String accessSecret = preferences.getString(ACCESS_SECRET_KEY, "");

                accessToken = new AccessToken(accessToken_,accessSecret);
                long userID = accessToken.getUserId();
                User user;
                try {
                    ConfigurationBuilder configBuilder = new ConfigurationBuilder();
                    configBuilder.setOAuthConsumerKey(consumerKey);
                    configBuilder.setOAuthConsumerSecret(consumerSecret);
                    twitter = new TwitterFactory(configBuilder.build()).getInstance(accessToken);
                    user = twitter.showUser(userID);
                    String name = user.getScreenName();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return urlString;
        }

        @Override
        public void onPostExecute(String result){
            if(!result.equals("")) {
                AuthorizeDialog dialog = new AuthorizeDialog();
                Bundle params = new Bundle();
                params.putString("AuthorizationURL", result);
                dialog.setArguments(params);
                dialog.show(getSupportFragmentManager(), "AuthorizeDialog");
            }
            else {
                Internet.retrofit = new MyRetrofitBuilder()
                        .getRetrofit("https://api.twitter.com/1.1/",
                                new MyOkHttpClient()
                                        .getClient(accessToken.getToken(), consumerSecret, accessToken.getTokenSecret()));

                Internet.service = Internet.retrofit.create(TwitterRest.class);

                new getAccauntInfo(MainActivity.this).execute((Void) null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        profile = menu.findItem(R.id.action_profile_view);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_log_out:
                if(requestToken != null){
                    String authorizationLink = "";
                    try {
                        authorizationLink = requestToken.getAuthorizationURL();
                    }catch(Exception e ){
                        e.printStackTrace();
                    }
                    AuthorizeDialog dialog = new AuthorizeDialog();
                    Bundle params = new Bundle();
                    params.putString("AuthorizationURL", authorizationLink);
                    dialog.setArguments(params);
                    dialog.show(getSupportFragmentManager(), "AuthorizeDialog");
                }
                setKeysAfterSuccessAuthorised("","",false);
                break;
            case R.id.action_send_new_tweet:
                startActivityForResult(new Intent(this, NewTweetActivity.class), NEW_TWEET);
                break;
            case R.id.action_profile_view:
                startActivityForResult(new Intent(this, ViewProfileActivity.class), VIEW_PROFILE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class AuthorizeDialog extends DialogFragment {

        public AuthorizeDialog(){}

        public Dialog onCreateDialog(Bundle params){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.browser_authorize, null, false);
            WebView webView = (WebView)v.findViewById(R.id.webView);
            dialog.setView(v);
            if(webView != null){
                webView.getSettings().setJavaScriptEnabled(true);
                Bundle param = getArguments();
                webView.loadUrl(param.getString("AuthorizationURL"));
                webView.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Uri uri = Uri.parse(url);
                            verifier = uri.getQueryParameter("oauth_verifier");
                            dismiss();
                            //revoke access token asynctask
                            new GetAccessToken((MainActivity) getActivity()).execute();
                        } else if (url.contains("denied")) {

                        }
                    }
                });
            }
            return dialog.create();
        }
    }

    public static class GetAccessToken extends AsyncTask<Void,Void,AccessToken>{

        MainActivity twitterActivity;

        public GetAccessToken(MainActivity activity){
            twitterActivity = activity;
        }

        @Override
        protected AccessToken doInBackground(Void... params) {
            try{
                accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            }catch(Exception e){
                e.printStackTrace();
            }
            return accessToken;
        }

        public void onPostExecute(AccessToken result){
            twitterActivity.setKeysAfterSuccessAuthorised(accessToken.getToken(),
                    accessToken.getTokenSecret(),true);
            Internet.retrofit = new MyRetrofitBuilder()
                    .getRetrofit("https://api.twitter.com/1.1/",
                            new MyOkHttpClient()
                                    .getClient(accessToken.getToken(),
                                            twitterActivity.consumerSecret,
                                            accessToken.getTokenSecret()));

            Internet.service = Internet.retrofit.create(TwitterRest.class);

            new getAccauntInfo(twitterActivity.getApplicationContext()).execute((Void) null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }
}
