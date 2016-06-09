package ua.tweets_reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ua.tweets_reader.dialogs.RetweetDialog;
import ua.tweets_reader.gson.current_user.User;
import ua.tweets_reader.gson.status.CommonStatusClass;
import ua.tweets_reader.gson.status.Medium;
import ua.tweets_reader.gson.status.Url;
import ua.tweets_reader.retrofit.TwitterRest;
import ua.tweets_reader.tweet.NewTweetActivity;

import com.dimka.twitt_reader.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;

public class ReviewCurrentTweet extends AppCompatActivity implements View.OnClickListener,
        RetweetDialog.onButtonsClickListener {


    ImageView statusLogo, imgAnswer,imgRetweet,imgFavorite,imgDelete,tweetImage;
    TextView txtName,txtScreenName,txtText,
            txtRetweetCount,txtFavoriteCount, txtStatusLink, dateCreate;
    ActionBar bar;
    Internet internetParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_current_tweet);
        internetParams = Internet.getInstance();
        bar = getSupportActionBar();
        if(bar != null){
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        initControls();
    }

    private void initControls(){
        statusLogo = (ImageView)findViewById(R.id.image_status_user_logo);
        imgAnswer = (ImageView)findViewById(R.id.image_answer_tweet);
        if(imgAnswer != null) {
            imgAnswer.setOnClickListener(this);
        }
        imgRetweet = (ImageView)findViewById(R.id.image_status_retweet);
        if(imgRetweet != null){
            imgRetweet.setOnClickListener(this);
        }
        imgFavorite = (ImageView)findViewById(R.id.image_status_favorite);
        if(imgFavorite != null){
            imgFavorite.setOnClickListener(this);
        }
        imgDelete = (ImageView)findViewById(R.id.delete_status);
        if(imgDelete != null){
            if(internetParams.getCurrentStatus().getUser().getIdStr().equals(internetParams.getCurrentUser().getIdStr())){
                imgDelete.setVisibility(View.VISIBLE);
            }
            imgDelete.setOnClickListener(this);
        }
        tweetImage = (ImageView)findViewById(R.id.tweet_image);

        txtName = (TextView)findViewById(R.id.status_user_name);
        txtScreenName = (TextView)findViewById(R.id.status_user_screen_name);
        txtText = (TextView)findViewById(R.id.status_text);
        txtRetweetCount = (TextView)findViewById(R.id.retweets_count);
        dateCreate = (TextView)findViewById(R.id.status_date_create);
        txtFavoriteCount = (TextView)findViewById(R.id.favorites_count);
        txtStatusLink = (TextView)findViewById(R.id.status_link);
        fillControls();
    }

    private void fillControls(){
        CommonStatusClass currentStatus = internetParams.getCurrentStatus();
        User statusUser = currentStatus.getUser();
        txtName.setText(statusUser.getName());
        String sn = "@" + statusUser.getScreenName();
        txtScreenName.setText(sn);

        txtRetweetCount.setText(String.valueOf(currentStatus.getRetweetCount()));
        dateCreate.setText(currentStatus.getCreatedAt().substring(0,currentStatus.getCreatedAt().indexOf("+")));
        txtFavoriteCount.setText(String.valueOf(currentStatus.getFavoriteCount()));
        if(!currentStatus.getUser().getIdStr().equals(Internet.getInstance().getCurrentUser().getIdStr())) {
            if (currentStatus.getRetweeted()) {
                imgRetweet.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_repeat_blue));
            } else {
                imgRetweet.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_repeat));
            }
        }else{
            imgRetweet.setEnabled(false);
        }
        if(currentStatus.getFavorited()) {
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_favorite_blue));
        }
        else{
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_favorite_border));
        }
        String mediaUrl = "";
        String message;
        String urlGetter;
        if(currentStatus.getEntities().getMedia().size() != 0) {
            if(currentStatus.getText().contains("#")) {
                for (Medium medium : currentStatus.getEntities().getMedia()) {
                    message = currentStatus.getText().substring(0, currentStatus.getText().indexOf("#"));
                    urlGetter = currentStatus.getText().substring(currentStatus.getText().indexOf("#"));
                    if (urlGetter.contains(medium.getUrl())) {
                        tweetImage.setVisibility(View.VISIBLE);
                        txtStatusLink.setText(urlGetter.replace(medium.getUrl(), ""));
                        txtText.setText(message);
                        mediaUrl = medium.getMediaUrlHttps();
                        break;
                    }
                }
            }
            else{
                txtText.setText(currentStatus.getText());
            }
        }
        else if(currentStatus.getEntities().getUrls().size() != 0){
            for (Url medium : currentStatus.getEntities().getUrls()) {
                if (currentStatus.getText().contains("#")){
                    message = currentStatus.getText().substring(0, currentStatus.getText().indexOf("#"));
                    urlGetter = currentStatus.getText().substring(currentStatus.getText().indexOf("#"));
                    if (checkTextForLink(urlGetter)) {
                        if (urlGetter.contains(medium.getUrl())) {
                            tweetImage.setVisibility(View.VISIBLE);
                            txtStatusLink.setText(urlGetter.replace(medium.getUrl(), ""));
                            txtText.setText(message);
                            mediaUrl = medium.getExpandedUrl();
                            break;
                        }
                    } else if (checkTextForLink(message)) {
                        txtStatusLink.setText(urlGetter);
                        urlGetter = message.substring(message.indexOf("http"));
                        message = message.substring(0, message.indexOf("http"));
                        if (urlGetter.contains(medium.getUrl())) {
                            tweetImage.setVisibility(View.VISIBLE);
                            txtText.setText(message);
                            mediaUrl = medium.getExpandedUrl();
                            break;
                        }
                    } else {
                        txtStatusLink.setText(urlGetter);
                        txtText.setText(message);
                    }
                }else{
                    txtText.setText(currentStatus.getText());
                    txtStatusLink.setVisibility(View.GONE);
                }
            }
        }
        else{
            txtText.setText(currentStatus.getText());
            txtStatusLink.setVisibility(View.GONE);
        }
        new ImagesLoader(this,statusLogo, tweetImage)
                .execute(statusUser.getProfileImageUrlHttps(),mediaUrl);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean checkTextForLink(String text){
        return text.contains("http");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        CommonStatusClass currentStatus = Internet.getInstance().getCurrentStatus();
        switch(id){
            case R.id.image_answer_tweet:
                startActivityForResult(new Intent(this, NewTweetActivity.class).putExtra(MainActivity.SCREEN_NAME,
                        currentStatus.getUser().getScreenName()), MainActivity.NEW_TWEET);
                break;
            case R.id.image_status_retweet:
                RetweetDialog dlg = new RetweetDialog();
                dlg.show(getSupportFragmentManager(),"questionDialog");
                break;
            case R.id.image_status_favorite:
                int retweetedCount = currentStatus.getFavoriteCount();
                boolean isAlreadyLiked = !currentStatus.getFavorited();
                if(isAlreadyLiked){
                    currentStatus.setFavoriteCount(retweetedCount + 1);
                    currentStatus.setFavorited(true);
                }else{
                    currentStatus.setFavoriteCount(retweetedCount - 1);
                    currentStatus.setFavorited(false);
                }
                new DoRetweetFavorite(this, !isAlreadyLiked, imgFavorite, false).execute(currentStatus.getId());
                break;
            case R.id.delete_status:
                new DestroyStatusAsync().execute(currentStatus.getId());
                break;
        }

    }

    @Override
    public void onQuoteButtonClick() {
        Intent intent = new Intent(this, NewTweetActivity.class);
        CommonStatusClass status = Internet.getInstance().getCurrentStatus();
        intent.putExtra(MainActivity.QUOTE_TEXT, status.getText());
        intent.putExtra(MainActivity.SCREEN_NAME, status.getUser().getScreenName());
        startActivityForResult(intent, MainActivity.NEW_TWEET);
    }

    @Override
    public void onRetweetButtonClick() {
        CommonStatusClass currentStatus = Internet.getInstance().getCurrentStatus();
        boolean isRetweeted = !currentStatus.getRetweeted();
        int retweetCount = currentStatus.getRetweetCount();
        if (isRetweeted) {
            currentStatus.setRetweetCount(retweetCount + 1);
        } else {
            currentStatus.setRetweetCount(retweetCount - 1);
        }
        currentStatus.setRetweeted(isRetweeted);
        new DoRetweetFavorite(this, !isRetweeted, imgRetweet, true).execute(currentStatus.getId());
    }

    private class DoRetweetFavorite extends AsyncTask<Long, Void,Void>{
        Boolean isRetweeted;
        CommonStatusClass successRetweeted;
        ImageView retweetImage;
        Context context;
        boolean isItRetweet;

        public DoRetweetFavorite(Context ctx, Boolean isRetweetedFavorite,
                                 ImageView imageRetweet, boolean isItRetweet){
            this.isRetweeted = isRetweetedFavorite;
            retweetImage = imageRetweet;
            context = ctx;
            this.isItRetweet = isItRetweet;
        }
        @Override
        protected Void doInBackground(Long... params) {
            long statusId = params[0];
            TwitterRest service = Internet.getInstance().getTwitterRest();
            if (!isRetweeted) {
                try {
                    Call<CommonStatusClass> retweetCall;
                    if (isItRetweet){
                        retweetCall = service.retweetStatus(statusId);
                    }
                    else{
                        retweetCall = service.createFavorite(statusId, true);
                    }
                    successRetweeted = retweetCall.execute().body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Call<CommonStatusClass> unretweetCall;
                if (isItRetweet){
                    unretweetCall =  service.unretweetStatus(statusId);
                }
                else{
                    unretweetCall =  service.destroyFavorite(statusId, true);
                }
                try{
                    successRetweeted = unretweetCall.execute().body();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(successRetweeted != null){
               /* for(CommonStatusClass status: homeStatuses){
                    if(status.getId() == successRetweeted.getId()){
                        int id = homeStatuses.indexOf(status);
                        homeStatuses.set(id, successRetweeted);
                        break;
                    }
                }*/
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            CommonStatusClass currentStatus = Internet.getInstance().getCurrentStatus();
            if(isItRetweet) {
                if (currentStatus.getRetweeted()) {
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_repeat_blue));
                } else {
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_repeat));
                }
            }else{
                if(currentStatus.getFavorited()) {
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_favorite_blue));
                }
                else{
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_favorite_border));
                }
            }
            txtRetweetCount.setText(String.valueOf(currentStatus.getRetweetCount()));
            txtFavoriteCount.setText(String.valueOf(currentStatus.getFavoriteCount()));
        }
    }

    private class DestroyStatusAsync extends AsyncTask<Long, Void,Void>{

        CommonStatusClass successRetweeted;

        @Override
        protected Void doInBackground(Long... params) {
            long statusId = params[0];

            Call<CommonStatusClass> destroyStatusCall;
            destroyStatusCall =  Internet.getInstance().getTwitterRest().destroyStatus(statusId);
            try{
                successRetweeted = destroyStatusCall.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }
            if(successRetweeted != null){
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            finish();
        }
    }

    private class ImagesLoader extends AsyncTask<String,Void,Void>{

        ImageView userLogo, statusImage;
        Context context;
        Bitmap bmpLogo = null;
        Bitmap bmpStatus = null;

        public ImagesLoader(Context context, ImageView... images){
            userLogo = images[0];
            statusImage = images[1];
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            String profileUrl = params[0];
            String statusImageUrl = params[1];
            if(!profileUrl.equals("")){
                try {
                    bmpLogo = Picasso.with(context).load(profileUrl).get();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            if(!statusImageUrl.equals("")){
                try {
                    bmpStatus = Picasso.with(context).load(statusImageUrl).get();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            userLogo.setImageBitmap(bmpLogo);
            if(bmpStatus != null) {
                statusImage.setImageBitmap(bmpStatus);
            }else{
                statusImage.setVisibility(View.GONE);
            }
        }
    }
}
