package com.dimka.twitt_reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimka.twitt_reader.dialogs.RetweetDialog;
import com.dimka.twitt_reader.pojo_classes.current_user.User;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.status.Medium;
import com.dimka.twitt_reader.pojo_classes.status.Url;
import com.dimka.twitt_reader.tweet.NewTweetActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;

public class ReviewCurrentTweet extends AppCompatActivity implements View.OnClickListener,
        RetweetDialog.onButtonsClickListener {


    ImageView statusLogo, imgAnswer,imgRetweet,imgFavorite,imgDelete,tweetImage;
    TextView txtName,txtScreenName,txtText,
            txtRetweetCount,txtFavoriteCount, txtStatusLink, dateCreate;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_current_tweet);
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
            imgFavorite.setOnClickListener(this/*new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v != null){}
                }
            }*/);
        }
        imgDelete = (ImageView)findViewById(R.id.delete_status);
        if(imgDelete != null){
            if(Internet.currentStatus.getUser().getIdStr().equals(Internet.currentUser.getIdStr())){
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
        User statusUser = Internet.currentStatus.getUser();
        txtName.setText(statusUser.getName());
        String sn = "@" + statusUser.getScreenName();
        txtScreenName.setText(sn);
       // txtText.setText(Internet.currentStatus.getText());
        txtRetweetCount.setText(String.valueOf(Internet.currentStatus.getRetweetCount()));
        dateCreate.setText(Internet.currentStatus.getCreatedAt().substring(0,Internet.currentStatus.getCreatedAt().indexOf("+")));
        txtFavoriteCount.setText(String.valueOf(Internet.currentStatus.getFavoriteCount()));
        if (Internet.currentStatus.getRetweeted()) {
            imgRetweet.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_repeat_blue_24dp));
        } else {
            imgRetweet.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_repeat_black_24dp));
        }
        if(Internet.currentStatus.getFavorited()) {
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_blue_24dp));
        }
        else{
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
        }
        String mediaUrl = "";
        String message = "";
        String urlGetter = "";
        if(Internet.currentStatus.getEntities().getMedia().size() != 0) {
            if(Internet.currentStatus.getText().contains("#")) {
                for (Medium medium : Internet.currentStatus.getEntities().getMedia()) {
                    message = Internet.currentStatus.getText().substring(0, Internet.currentStatus.getText().indexOf("#"));
                    urlGetter = Internet.currentStatus.getText().substring(Internet.currentStatus.getText().indexOf("#"));
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
                txtText.setText(Internet.currentStatus.getText());
            }
        }
        else if(Internet.currentStatus.getEntities().getUrls().size() != 0){
            for (Url medium : Internet.currentStatus.getEntities().getUrls()) {
                if (Internet.currentStatus.getText().contains("#")){
                    message = Internet.currentStatus.getText().substring(0, Internet.currentStatus.getText().indexOf("#"));
                    urlGetter = Internet.currentStatus.getText().substring(Internet.currentStatus.getText().indexOf("#"));
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
                    txtText.setText(Internet.currentStatus.getText());
                    txtStatusLink.setVisibility(View.GONE);
                }
            }
        }
        else{
            txtText.setText(Internet.currentStatus.getText());
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
        switch(id){
            case R.id.image_answer_tweet:
                startActivityForResult(new Intent(this, NewTweetActivity.class).putExtra(MainActivity.SCREEN_NAME,
                        Internet.currentStatus.getUser().getScreenName()), MainActivity.NEW_TWEET);
                break;
            case R.id.image_status_retweet:
                RetweetDialog dlg = new RetweetDialog();
                dlg.show(getSupportFragmentManager(),"questiobDialog");
                break;
            case R.id.image_status_favorite:
                int retweetedCount = Internet.currentStatus.getFavoriteCount();
                boolean isAlreadyLiked = !Internet.currentStatus.getFavorited();
                if(isAlreadyLiked){
                    Internet.currentStatus.setFavoriteCount(retweetedCount + 1);
                    Internet.currentStatus.setFavorited(true);
                }else{
                    Internet.currentStatus.setFavoriteCount(retweetedCount - 1);
                    Internet.currentStatus.setFavorited(false);
                }
                new DoRetweetFavorite(this, !isAlreadyLiked, imgFavorite, false).execute(Internet.currentStatus.getId());
                break;
            case R.id.delete_status:
                new DestroyStatusAsync().execute(Internet.currentStatus.getId());
                break;
        }

    }

    @Override
    public void onQuoteButtonClick() {
        startActivityForResult(new Intent(this, NewTweetActivity.class).putExtra(MainActivity.QUOTE_TEXT,
                Internet.currentStatus.getText()).putExtra(MainActivity.SCREEN_NAME,
                Internet.currentStatus.getUser().getScreenName()), MainActivity.NEW_TWEET);
    }

    @Override
    public void onRetweetButtonClick() {
        boolean isRetweeted = !Internet.currentStatus.getRetweeted();
        int retweetCount = Internet.currentStatus.getRetweetCount();
        if (isRetweeted) {
            Internet.currentStatus.setRetweetCount(retweetCount + 1);
        } else {
            Internet.currentStatus.setRetweetCount(retweetCount - 1);
        }
        Internet.currentStatus.setRetweeted(isRetweeted);
        new DoRetweetFavorite(this, !isRetweeted, imgRetweet, true).execute(Internet.currentStatus.getId());
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
            if (!isRetweeted) {
                try {
                    Call<CommonStatusClass> retweetCall;
                    if (isItRetweet){
                        retweetCall = Internet.service.retweetStatus(statusId);
                    }
                    else{
                        retweetCall = Internet.service.createFavorite(statusId, true);
                    }
                    successRetweeted = retweetCall.execute().body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Call<CommonStatusClass> unretweetCall;
                if (isItRetweet){
                    unretweetCall =  Internet.service.unretweetStatus(statusId);
                }
                else{
                    unretweetCall =  Internet.service.destroyFavorite(statusId, true);
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
            if(isItRetweet) {
                if (Internet.currentStatus.getRetweeted()) {
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_blue_24dp));
                } else {
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_black_24dp));
                }
            }else{
                if(Internet.currentStatus.getFavorited()) {
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_blue_24dp));
                }
                else{
                    retweetImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_black_24dp));
                }
            }
            txtRetweetCount.setText(String.valueOf(Internet.currentStatus.getRetweetCount()));
            txtFavoriteCount.setText(String.valueOf(Internet.currentStatus.getFavoriteCount()));
        }
    }

    private class DestroyStatusAsync extends AsyncTask<Long, Void,Void>{

        CommonStatusClass successRetweeted;

        @Override
        protected Void doInBackground(Long... params) {
            long statusId = params[0];

            Call<CommonStatusClass> destroyStatusCall;
            destroyStatusCall =  Internet.service.destroyStatus(statusId);
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
