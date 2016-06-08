package com.dimka.twitt_reader.list_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimka.twitt_reader.ImageLoader;
import com.dimka.twitt_reader.Internet;
import com.dimka.twitt_reader.R;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.status.Medium;
import com.dimka.twitt_reader.pojo_classes.status.Url;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;

/*
 * Created by Dimka on 29.05.2016.
 */
public class TweetsViewAdapter extends BaseAdapter {

    public interface onAnswerClickListener{
        void onAnsverClick(CommonStatusClass status);
        void onRetweetClick(CommonStatusClass status);
        void onFavoriteClick();
    }

    public interface onTweetAvatarClickListener{
        void onAvatarClick();
    }

    List<CommonStatusClass> statuses;
    CommonStatusClass favoriteStatus;
    Context context;
    LayoutInflater inflater;
    View v;
    boolean isProfileTweets;

    ImageView imgAvatar, imgOfTweet, imgAnswer, imgRetweet, imgLike, imgAddFriend;
    TextView txtFullName,txtscreenName,txtTweetText, tweet_link, txtTweetUserLink,
            retweetsCount, favoriteCount;

    public TweetsViewAdapter(Context ctx, List<CommonStatusClass> statuses, boolean isItProfileTweets){
        this.statuses = statuses;
        context = ctx;
        isProfileTweets = isItProfileTweets;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public CommonStatusClass getItem(int position) {
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        v = convertView != null ? convertView : inflater.inflate(R.layout.tweet_view_list_item, parent,false);
        initControls(v);
        CommonStatusClass currentStatus = statuses.get(position);
        fillControls(currentStatus);
        v.setTag(currentStatus);
        return v;
    }

    private void initControls(View v){
        txtFullName = (TextView) v.findViewById(R.id.full_name);
        txtscreenName = (TextView) v.findViewById(R.id.screen_name);
        txtTweetText  =(TextView) v.findViewById(R.id.tweet_text);
        tweet_link = (TextView) v.findViewById(R.id.tweet_link);
        txtTweetUserLink = (TextView) v.findViewById(R.id.tweet_user_link);
        imgAvatar = (ImageView)v.findViewById(R.id.img_avatar);
        imgOfTweet = (ImageView)v.findViewById(R.id.img_of_tweet);
        imgAnswer = (ImageView)v.findViewById(R.id.img_answer_tweet);
        imgRetweet = (ImageView)v.findViewById(R.id.img_retweet);
        imgLike = (ImageView)v.findViewById(R.id.img_like_tweet);
        imgAddFriend = (ImageView)v.findViewById(R.id.img_add_friend);
        retweetsCount = (TextView)v.findViewById(R.id.retweets_count);
        favoriteCount  = (TextView)v.findViewById(R.id.likes_count);
    }

    private void fillControls(CommonStatusClass status){
        txtFullName.setText(status.getUser().getName());
        String screenNAme = "@" + status.getUser().getScreenName();
        txtscreenName.setText(screenNAme);
        //txtTweetText.setText(status.getText());
        if(!status.getUser().getIdStr().equals(Internet.currentUser.getIdStr())) {
            if (status.getRetweeted()) {
                imgRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_blue_24dp));
            } else {
                imgRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_black_24dp));
            }
            imgRetweet.setEnabled(true);
        }
        else{
            imgRetweet.setEnabled(false);
        }
        if(status.getFavorited()) {
            imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_blue_24dp));
        }
        else{
            imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_black_24dp));
        }

        retweetsCount.setText(String.valueOf(status.getRetweetCount()));
        favoriteCount.setText(String.valueOf(status.getFavoriteCount()));
        tweet_link.setVisibility(View.GONE);
        txtTweetUserLink.setVisibility(View.GONE);
        txtTweetUserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ((TextView)v).getText().toString();
                context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            }
        });

        for (Url url: status.getEntities().getUrls()){
            if(status.getText().contains(url.getUrl())){
                tweet_link.setVisibility(View.VISIBLE);
                tweet_link.setText(url.getDisplayUrl());
                break;
            }
        }

        imgOfTweet.setVisibility(View.GONE);
        String tweetText = "";
        String linkText = "";
        String userLink;
        String statusText = status.getText();
        if(status.getEntities().getMedia().size() != 0) {
            for (Medium url : status.getEntities().getMedia()) {
                if (!statusText.equals("")) {
                    if(statusText.contains("#")) {
                        tweetText = statusText.substring(0, statusText.indexOf("#"));
                        linkText = statusText.substring(statusText.indexOf("#"));
                        userLink = linkText.substring(0, linkText.indexOf("http"));
                        if (linkText.contains(url.getUrl())) {
                            txtTweetText.setText(tweetText);
                            imgOfTweet.setVisibility(View.VISIBLE);
                            txtTweetUserLink.setText(userLink);
                            txtTweetUserLink.setVisibility(View.VISIBLE);
                            new ImageLoader(context, imgOfTweet).execute(url.getMediaUrlHttps());
                            break;
                        }
                    }
                    else{
                        tweetText = status.getText();
                        txtTweetText.setText(tweetText);
                    }
                }
            }
        }
        else{
            txtTweetText.setText(statusText);
        }

        imgAvatar.setTag(status);
        if(!status.getUser().getProfileImageUrlHttps().equals("")) {
            if(!isProfileTweets) {
                imgAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Internet.otherUser = ((CommonStatusClass) v.getTag()).getUser();
                        ((onTweetAvatarClickListener) context).onAvatarClick();
                    }
                });
            }
            //load Image
            new ImageLoader(context, imgAvatar).execute(status.getUser().getProfileImageUrlHttps());
        }
        else{
            imgAvatar.setVisibility(View.GONE);
        }
        imgRetweet.setTag(status);
        imgRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStatusClass status = (CommonStatusClass)v.getTag();
                ((onAnswerClickListener)context).onRetweetClick(status);
            }
        });
        imgLike.setTag(status);
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStatusClass status = (CommonStatusClass)v.getTag();
                Internet.currentStatus = status;
                new LikeUnlike(status.getId()).execute(status.getFavorited());
            }
        });

        imgAnswer.setTag(status);
        imgAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStatusClass status = (CommonStatusClass)v.getTag();
                ((onAnswerClickListener)context).onAnsverClick(status);
            }
        });
        //imgAddFriend
    }

    private class LikeUnlike extends AsyncTask<Boolean, Void,Void>{

        long id;

        public LikeUnlike(long statusId){
            id = statusId;
        }
        @Override
        protected Void doInBackground(Boolean... params) {
            boolean isAlreadyLiked = params[0];
            Call<CommonStatusClass> status;
            int retweetedCount = Internet.currentStatus.getFavoriteCount();
            if(!isAlreadyLiked){
                status = Internet.service.createFavorite(id,true);
                Internet.currentStatus.setFavoriteCount(retweetedCount + 1);
                Internet.currentStatus.setFavorited(true);
            }else{
                status = Internet.service.destroyFavorite(id,true);
                Internet.currentStatus.setFavoriteCount(retweetedCount - 1);
                Internet.currentStatus.setFavorited(false);
            }

            try{
                favoriteStatus = status.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result){
            if(favoriteStatus != null){
                ((onAnswerClickListener)context).onFavoriteClick();
            }
        }
    }


    private class UserGetter extends AsyncTask<Boolean, Void,Void>{

        long id;

        public UserGetter(long statusId){
            id = statusId;
        }
        @Override
        protected Void doInBackground(Boolean... params) {
            boolean isAlreadyLiked = params[0];
            Call<CommonStatusClass> status;
            int retweetedCount = Internet.currentStatus.getFavoriteCount();
            if(!isAlreadyLiked){
                status = Internet.service.createFavorite(id,true);
                Internet.currentStatus.setFavoriteCount(retweetedCount + 1);
                Internet.currentStatus.setFavorited(true);
            }else{
                status = Internet.service.destroyFavorite(id,true);
                Internet.currentStatus.setFavoriteCount(retweetedCount - 1);
                Internet.currentStatus.setFavorited(false);
            }

            try{
                favoriteStatus = status.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result){
            if(favoriteStatus != null){
                ((onAnswerClickListener)context).onFavoriteClick();
            }
        }
    }

}
