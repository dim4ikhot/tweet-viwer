package ua.tweetsreader;

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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ua.tweetsreader.dialogs.InfoDialog;
import ua.tweetsreader.dialogs.RetweetDialog;
import ua.tweetsreader.adapters.TweetsViewAdapter;
import ua.tweetsreader.networking.InternetConnectionChecker;
import ua.tweetsreader.networking.MyOkHttpClient;
import ua.tweetsreader.networking.MyRetrofitBuilder;
import ua.tweetsreader.gson.accountsettings.AccountSettings;
import ua.tweetsreader.gson.user.User;
import ua.tweetsreader.gson.status.CommonStatusClass;
import ua.tweetsreader.gson.credentials.VerifyCredentials;
import ua.tweetsreader.retrofit.TwitterRest;
import ua.tweetsreader.tweet.NewTweetActivity;

import com.dimka.twitt_reader.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements TweetsViewAdapter.onAnswerClickListener,
        RetweetDialog.onButtonsClickListener, TweetsViewAdapter.onTweetAvatarClickListener {

    static String CALLBACK_URL = "x-oauthflow-twitter://twitterlogin";
    public static String ACCESS_TOKEN_KEY = "access_token_key";
    public static String ACCESS_SECRET_KEY = "access_token_secret";


    public static String IS_AUTHORIZED_KEY = "is_authorized";

    public static final String SCREEN_NAME = "screen_name";
    public static final String QUOTE_TEXT= "quote_tweet";
    public static int NEW_TWEET = 0;
    public static int VIEW_PROFILE = 1;
    public static int SEARCH_USERS = 2;
    public static int REVIEW_TWEET = 3;
    public static String LAST_VISIBLE = "last_visible";


    static String verifier;
    SharedPreferences preferences;

    public static Twitter twitter;
    static RequestToken requestToken;
    static AccessToken accessToken;

    SharedPreferences pref;
    String consumerKey;
    String consumerSecret;
    TextView noConnection;
    ProgressBar progressBar;
    ListView homeTimeline;
    Button newTweetBtn;
    TweetsViewAdapter adapter;
    boolean authorized = false;
    List<CommonStatusClass> homeStatuses = new ArrayList<>();
    Internet internetParams;
    AuthorizeDialog authorizationDialog;

    MenuItem profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        internetParams = Internet.getInstance();
        homeTimeline = (ListView)(findViewById(R.id.all_tweets));
        homeTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonStatusClass status = (CommonStatusClass) view.getTag();
                if(status != null) {
                    internetParams.setCurrentStatus(status);
                    startActivityForResult(new Intent(MainActivity.this,
                            ReviewCurrentTweet.class), REVIEW_TWEET);
                }
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        newTweetBtn = (Button)findViewById(R.id.button_new_tweet);
        newTweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSendNewTweet();
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        consumerKey = getResources().getString(R.string.consumer_key);
        consumerSecret = getResources().getString(R.string.consumer_secret);
        noConnection = (TextView)findViewById(R.id.txtNoInternetConnection);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        noConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                homeTimeline.setVisibility(View.GONE);
                getAccessToken();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(new InternetConnectionChecker(this).isConnected()) {
            noConnection.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getAccessToken();
        }
        else{
            noConnection.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

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

    @Override
    public void onDestroy(){
        SharedPreferences.Editor edit = pref.edit();
        int last = homeTimeline.getFirstVisiblePosition();
        edit.putInt(LAST_VISIBLE, last);
        edit.apply();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle params){
        if(authorizationDialog != null && !authorizationDialog.isHidden()) {
            authorizationDialog.dismiss();
            authorizationDialog = null;
        }
        super.onSaveInstanceState(params);
    }

    @Override
    public void onAvatarClick() {
        startActivityForResult(new Intent(this, ViewProfileActivity.class).putExtra("isShowMySelf",false), VIEW_PROFILE);
    }

    @Override
    public void onRetweetClick(CommonStatusClass status) {
        RetweetDialog dlg = new RetweetDialog();
        internetParams.setCurrentStatus(status);
        dlg.show(getSupportFragmentManager(),"questiobDialog");
    }

    @Override
    public void onFavoriteClick() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRetweetButtonClick() {
        CommonStatusClass currentStatus = internetParams.getCurrentStatus();
        boolean isRetweeted = !currentStatus.getRetweeted();
        int retweetCount = currentStatus.getRetweetCount();
        if (isRetweeted) {
            currentStatus.setRetweetCount(retweetCount + 1);
        } else {
            currentStatus.setRetweetCount(retweetCount - 1);
        }
        currentStatus.setRetweeted(isRetweeted);
        new RetweetUnRetweeAsync(!isRetweeted,adapter).execute(currentStatus.getId());
    }

    @Override
    public void onAnswerClick(CommonStatusClass status) {
        startActivityForResult(new Intent(this, NewTweetActivity.class).putExtra(SCREEN_NAME,
                status.getUser().getScreenName()), NEW_TWEET);
    }

    @Override
    public void onQuoteButtonClick() {
        Intent intent = new Intent(this, NewTweetActivity.class);
        intent.putExtra(QUOTE_TEXT,internetParams.getCurrentStatus().getText());
        intent.putExtra(SCREEN_NAME,internetParams.getCurrentStatus().getUser().getScreenName());
        startActivityForResult(intent, NEW_TWEET);
    }

    public static class getAccountInfo extends AsyncTask<Void,Void,String>{

        Context context;
        Bitmap bmp = null;
        MainActivity activeActivity;

        public getAccountInfo(Context ctx){
            context = ctx;
            activeActivity = (MainActivity)context;
        }

        @Override
        protected String doInBackground(Void... params) {
            String profileImage = "";
            TwitterRest service = Internet.getInstance().getTwitterRest();
            Call<AccountSettings> result = service.accauntSettings();
            Call<VerifyCredentials> verifyCredentialsCall = service.getVerifyCredentials();
            Call<List<CommonStatusClass>> callHomeStatuses = service.getHomeTimeline(100);
            try {
                AccountSettings temp = result.execute().body();
                Call<User> userCall = null;
                if(temp != null) {
                    userCall = service.getUser(temp.getScreenName());
                }
                Internet internet = Internet.getInstance();
                if(userCall!= null) {
                    internet.setCurrentUser(userCall.execute().body());
                }
                internet.setCredentials(verifyCredentialsCall.execute().body());

                if(internet.getCurrentUser() != null) {
                    profileImage = internet.getCurrentUser().getProfileImageUrlHttps();
                }
                activeActivity.homeStatuses = callHomeStatuses.execute().body();
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
            return Internet.getInstance().getCurrentUser().getName();
        }
        @Override
        protected void onPostExecute(String result){
            String title = "";
            User currentUser = Internet.getInstance().getCurrentUser();
            if(currentUser != null) {
                title = currentUser.getName().length() > 20 ?
                        currentUser.getName().substring(0, 19) + "..." :
                        currentUser.getName();
                activeActivity.authorized = true;
            }
            activeActivity.profile.setTitle(title);
            if(bmp != null) {
                BitmapDrawable bmpDrawable = new BitmapDrawable(context.getResources(), bmp);
                activeActivity.profile.setIcon(bmpDrawable);
            }

            activeActivity.progressBar.setVisibility(View.GONE);
            activeActivity.noConnection.setVisibility(View.GONE);

            if(activeActivity.homeStatuses != null && activeActivity.homeStatuses.size() > 0){
                activeActivity.adapter = new TweetsViewAdapter(context, activeActivity.homeStatuses,false);
                activeActivity.homeTimeline.setAdapter(((MainActivity)context).adapter);
                int to = getTweetPosition(activeActivity, activeActivity.homeStatuses);
                if(to != -1){
                    activeActivity.homeTimeline.setSelection(to);
                }
                activeActivity.homeTimeline.setVisibility(View.VISIBLE);
                activeActivity.newTweetBtn.setVisibility(View.GONE);
                Internet.getInstance().setCurrentStatus(null);
            }
            else{
                activeActivity.homeTimeline.setVisibility(View.GONE);
                if(activeActivity.authorized) {
                    activeActivity.newTweetBtn.setVisibility(View.VISIBLE);
                }
                else{
                    activeActivity.newTweetBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    public static int getTweetPosition(Context ctx, List<CommonStatusClass> tweets){
        CommonStatusClass currentStatus = Internet.getInstance().getCurrentStatus();
        if(currentStatus != null) {
            for (CommonStatusClass status : tweets) {
                if (status.getId() == currentStatus.getId()) {
                    return tweets.indexOf(status);
                }
            }
        }
        else{
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            int visible = pref.getInt(MainActivity.LAST_VISIBLE, -1);
            if(visible != -1){
                return visible;
            }
        }
        return -1;
    }

    private void getAccessToken(){
        new Authorization().execute((Void) null);
    }

    public boolean checkIsAuthorized(){
        return preferences.getBoolean(IS_AUTHORIZED_KEY, false);
    }

    public void setKeysAfterSuccessAuthorised(String accessToken, String tokenSecret, boolean isAuthorized){
        SharedPreferences.Editor e = preferences.edit();
        e.putString(ACCESS_TOKEN_KEY, accessToken);
        e.putString(ACCESS_SECRET_KEY, tokenSecret);
        e.putBoolean(IS_AUTHORIZED_KEY, isAuthorized);
        e.apply();
    }

    public class Authorization extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            String urlString = "";
            //if not authorized - show authentication dialog
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
                try {
                    ConfigurationBuilder configBuilder = new ConfigurationBuilder();
                    configBuilder.setOAuthConsumerKey(consumerKey);
                    configBuilder.setOAuthConsumerSecret(consumerSecret);
                    twitter = new TwitterFactory(configBuilder.build()).getInstance(accessToken);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return urlString;
        }

        @Override
        public void onPostExecute(String result){
            if(!result.equals("")) {
                authorizationDialog = new AuthorizeDialog();
                Bundle params = new Bundle();
                params.putString("AuthorizationURL", result);
                authorizationDialog.setArguments(params);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(authorizationDialog, "AuthorizeDialog");
                transaction.commitAllowingStateLoss();

                //authorizationDialog.show(getSupportFragmentManager(), "AuthorizeDialog");
            }
            else {
                Retrofit retrofit = new MyRetrofitBuilder()
                        .getRetrofit("https://api.twitter.com/1.1/",
                                new MyOkHttpClient()
                                        .getClient(accessToken.getToken(), consumerSecret, accessToken.getTokenSecret()));
                Internet.getInstance().setRetrofit(retrofit);
                TwitterRest service = retrofit.create(TwitterRest.class);
                Internet.getInstance().setTwitterRest(service);

                new getAccountInfo(MainActivity.this).execute((Void) null);
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

    private void actionSendNewTweet(){
        InfoDialog dialogInfo = new InfoDialog();
        String title = getResources().getString(R.string.not_authorized_title);
        dialogInfo.setTitle(title);
        String message;
        if(authorized) {
            startActivityForResult(new Intent(this, NewTweetActivity.class), NEW_TWEET);
        }
        else{
            title = getResources().getString(R.string.not_authorized_tweet_title);
            dialogInfo.setTitle(title);
            message = getResources().getString(R.string.not_authorized_message);
            dialogInfo.setMessage(message);
            dialogInfo.show(getSupportFragmentManager(), "not authorized");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        InfoDialog dialogInfo = new InfoDialog();
        String title = getResources().getString(R.string.not_authorized_title);
        dialogInfo.setTitle(title);
        String message = getResources().getString(R.string.not_authorized_message);
        dialogInfo.setMessage(message);

        switch(id) {
            case R.id.action_log_out:
                setKeysAfterSuccessAuthorised("","",false);
                authorized = false;
                getAccessToken();
                break;
            case R.id.action_send_new_tweet:
                /*
                if(authorized) {
                    startActivityForResult(new Intent(this, NewTweetActivity.class), NEW_TWEET);
                }
                else{
                    title = getResources().getString(R.string.not_authorized_tweet_title);
                    dialogInfo.setTitle(title);
                    dialogInfo.show(getSupportFragmentManager(), "not authorized");
                }
                */
                actionSendNewTweet();
                break;
            case R.id.action_profile_view:
                if(authorized) {
                    startActivityForResult(new Intent(this, ViewProfileActivity.class).putExtra("isDhowMySelf", true), VIEW_PROFILE);
                }
                else{
                    dialogInfo.show(getSupportFragmentManager(), "not authorized");
                }
                break;
            case R.id.action_search:
                if(authorized) {
                    startActivityForResult(new Intent(this, SearchActivity.class), SEARCH_USERS);
                }
                else{
                    title = getResources().getString(R.string.not_authorized_search_title);
                    dialogInfo.setTitle(title);
                    dialogInfo.show(getSupportFragmentManager(), "not authorized");
                }
                break;

            case R.id.action_refresh_tweets_list:
                progressBar.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                homeTimeline.setVisibility(View.GONE);
                if(authorized) {
                    new getAccountInfo(this).execute((Void) null);
                }
                else{
                    getAccessToken();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class AuthorizeDialog extends DialogFragment {

        public AuthorizeDialog(){}
        @NonNull
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
                        if (url.contains("oauth_verifier") && (!authComplete)) {
                            authComplete = true;
                            Uri uri = Uri.parse(url);
                            verifier = uri.getQueryParameter("oauth_verifier");
                            dismiss();
                            //revoke access token async task
                            new GetAccessToken((MainActivity) getActivity()).execute();
                        } else if (url.contains("denied")) {
                            ((MainActivity)getActivity()).progressBar.setVisibility(View.GONE);
                            dismiss();
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
            Retrofit retrofit = new MyRetrofitBuilder()
                    .getRetrofit("https://api.twitter.com/1.1/",
                            new MyOkHttpClient()
                                    .getClient(accessToken.getToken(),
                                            twitterActivity.consumerSecret,
                                            accessToken.getTokenSecret()));

            TwitterRest service = retrofit.create(TwitterRest.class);
            Internet.getInstance().setRetrofit(retrofit);
            Internet.getInstance().setTwitterRest(service);

            new getAccountInfo(twitterActivity).execute((Void) null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);
        noConnection.setVisibility(View.GONE);
        homeTimeline.setVisibility(View.GONE);
        if(new InternetConnectionChecker(this).isConnected()) {
            new getAccountInfo(this).execute((Void) null);
        }
        else{
            progressBar.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
            homeTimeline.setVisibility(View.GONE);
        }

    }
}
