package com.dimka.twitt_reader.list_adapters;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimka.twitt_reader.R;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.verify_credentials.Status;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
 * Created by Dimka on 29.05.2016.
 */
public class TweetsViewAdapter extends BaseAdapter {

    List<CommonStatusClass> statuses;
    Context context;
    LayoutInflater inflater;
    View v;

    ImageView imgAvatar, imgOfTweet, imgAnswer, imgRetweet, imgLike, imgAddFriend;
    TextView txtFullName,txtscreenName,txtTweetText, tweet_link, txtTweetUserLink;

    public TweetsViewAdapter(Context ctx, List<CommonStatusClass> statuses){
        this.statuses = statuses;
        context = ctx;
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
    }

    private void fillControls(CommonStatusClass status){
        txtFullName.setText(status.getUser().getName());
        String screenNAme = "@" + status.getUser().getScreenName();
        txtscreenName.setText(screenNAme);
        txtTweetText.setText(status.getText());

        tweet_link.setVisibility(View.GONE);
        txtTweetUserLink.setVisibility(View.GONE);
        if(!status.getUser().getProfileImageUrlHttps().equals("")) {
            //load Image
            new ImageLoader(context, imgAvatar).execute(status.getUser().getProfileImageUrlHttps());
        }
        else{
            imgAvatar.setVisibility(View.GONE);
        }
        imgOfTweet.setVisibility(View.GONE);
        //imgRetweet
        //imgLike
        //imgAddFriend
    }

    public class ImageLoader extends AsyncTask<String,Void,Void>{

        Context context;
        Bitmap bmp;
        ImageView headerImage;

        public ImageLoader(Context ctx, ImageView headerImg){
            context = ctx;
            headerImage = headerImg;
        }

        @Override
        protected Void doInBackground(String... params) {
            String profileImage = params[0];
            try {
                bmp = Picasso.with(context).load(profileImage).get();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Void result){
            if(bmp != null) {
                headerImage.setImageBitmap(bmp);
            }
        }
    }
}
