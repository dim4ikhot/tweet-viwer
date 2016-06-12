package ua.tweetsreader.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimka.twitt_reader.R;

import ua.tweetsreader.ImageLoader;
import ua.tweetsreader.Internet;
import ua.tweetsreader.gson.status.CommonStatusClass;
import ua.tweetsreader.gson.status.Medium;
import ua.tweetsreader.gson.status.Url;

import java.util.List;

import retrofit2.Call;

/*
 * Created by Dimka on 29.05.2016.
 */
public class TweetsViewAdapter extends BaseAdapter {

    public interface onAnswerClickListener{
        void onAnswerClick(CommonStatusClass status);
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
    TextView userFullName,userScreenName,tweetText, tweetLink, tweetUserLink,
            retweetsCount, favoriteCount;

    Internet internetParams;

    public TweetsViewAdapter(Context ctx, List<CommonStatusClass> statuses, boolean isItProfileTweets){
        this.statuses = statuses;
        context = ctx;
        isProfileTweets = isItProfileTweets;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        internetParams = Internet.getInstance();
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
        userFullName = (TextView) v.findViewById(R.id.full_name);
        userScreenName = (TextView) v.findViewById(R.id.screen_name);
        tweetText  =(TextView) v.findViewById(R.id.tweet_text);
        tweetLink = (TextView) v.findViewById(R.id.tweet_link);
        tweetUserLink = (TextView) v.findViewById(R.id.tweet_user_link);
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
        userFullName.setText(status.getUser().getName());
        String screenNAme = "@" + status.getUser().getScreenName();
        userScreenName.setText(screenNAme);
        //txtTweetText.setText(status.getText());
        if(!status.getUser().getIdStr().equals(internetParams.getCurrentUser().getIdStr())) {
            if (status.getRetweeted()) {
                imgRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_repeat_blue));
            } else {
                imgRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_repeat));
            }
            imgRetweet.setEnabled(true);
        }
        else{
            imgRetweet.setEnabled(false);
        }
        if(status.getFavorited()) {
            imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_favorite_blue));
        }
        else{
            imgLike.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_favorite_border));
        }

        retweetsCount.setText(String.valueOf(status.getRetweetCount()));
        favoriteCount.setText(String.valueOf(status.getFavoriteCount()));
        tweetLink.setVisibility(View.GONE);
        tweetUserLink.setVisibility(View.GONE);
        tweetUserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ((TextView)v).getText().toString();
                context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            }
        });

        for (Url url: status.getEntities().getUrls()){
            if(status.getText().contains(url.getUrl())){
                tweetLink.setVisibility(View.VISIBLE);
                tweetLink.setText(url.getDisplayUrl());
                break;
            }
        }

        imgOfTweet.setVisibility(View.GONE);
        String tweetText;
        String linkText;
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
                            this.tweetText.setText(tweetText);
                            imgOfTweet.setVisibility(View.VISIBLE);
                            tweetUserLink.setText(userLink);
                            tweetUserLink.setVisibility(View.VISIBLE);
                            new ImageLoader(context, imgOfTweet).execute(url.getMediaUrlHttps());
                            break;
                        }
                    }
                    else{
                        tweetText = status.getText();
                        this.tweetText.setText(tweetText);
                    }
                }
            }
        }
        else{
            this.tweetText.setText(statusText);
        }

        imgAvatar.setTag(status);
        if(!status.getUser().getProfileImageUrlHttps().equals("")) {
            if(!isProfileTweets) {
                imgAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        internetParams.setOtherUser(((CommonStatusClass) v.getTag()).getUser());
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
                internetParams.setCurrentStatus(status);
                new LikeUnlike(status.getId()).execute(status.getFavorited());
            }
        });

        imgAnswer.setTag(status);
        imgAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonStatusClass status = (CommonStatusClass)v.getTag();
                ((onAnswerClickListener)context).onAnswerClick(status);
            }
        });
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
            int retweetedCount = internetParams.getCurrentStatus().getFavoriteCount();
            CommonStatusClass currentStatus = internetParams.getCurrentStatus();
            if(!isAlreadyLiked){
                status = internetParams.getTwitterRest().createFavorite(id,true);
                currentStatus.setFavoriteCount(retweetedCount + 1);
                currentStatus.setFavorited(true);
            }else{
                status = internetParams.getTwitterRest().destroyFavorite(id,true);
                currentStatus.setFavoriteCount(retweetedCount - 1);
                currentStatus.setFavorited(false);
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
