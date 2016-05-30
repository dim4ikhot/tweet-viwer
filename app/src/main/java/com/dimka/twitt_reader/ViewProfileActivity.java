package com.dimka.twitt_reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimka.twitt_reader.list_adapters.TweetsViewAdapter;
import com.dimka.twitt_reader.pojo_classes.Error.Error;
import com.dimka.twitt_reader.pojo_classes.Error.Errors;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.timeline.user_timeline.UserTimeline;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileActivity extends AppCompatActivity {

    ImageView headerImage, profileImage;
    Button editProfile;
    TextView readingCount, readersCount, fullName, screenName;
    ListView usersTweets;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
        initControls();
    }

    private void initControls(){
        headerImage = (ImageView)findViewById(R.id.image_header);
        profileImage = (ImageView)findViewById(R.id.profile_image);
        editProfile = (Button)findViewById(R.id.button_edit_profile);
        readingCount = (TextView) findViewById(R.id.profile_reading_count_value);
        readersCount = (TextView)findViewById(R.id.profile_readers_count_value);
        fullName = (TextView)findViewById(R.id.profile_full_name);
        screenName = (TextView)findViewById(R.id.profile_screen_name);
        usersTweets = (ListView)findViewById(R.id.usersTweets);
        fillsControl();
    }

    private void fillsControl(){
        new LoadProfileParams(this).execute((Void)null);
    }

    private class LoadProfileParams extends AsyncTask<Void,Void,Void>{
        Context context;
        Bitmap bmpBackground, bmpProfileIcon;

        public LoadProfileParams(Context ctx){
            context = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(Internet.verifyCredentials != null){
                String backgroundURL = Internet.verifyCredentials.getProfileBackgroundImageUrlHttps();
                String profileImage = Internet.verifyCredentials.getProfileImageUrlHttps();
                try {
                    bmpBackground = Picasso.with(context).load(backgroundURL).get();
                    bmpProfileIcon = Picasso.with(context).load(profileImage).get();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(bmpBackground != null) {
                headerImage.setImageBitmap(bmpBackground);
            }
            if(bmpProfileIcon != null) {
                profileImage.setImageBitmap(bmpProfileIcon);
            }
            String reading = "" + Internet.verifyCredentials.getFriendsCount();
            readingCount.setText(reading);
            String readers = "" + Internet.verifyCredentials.getFollowersCount();
            readersCount.setText(readers);
            fullName.setText(Internet.verifyCredentials.getName());
            String screenNameText = "@"+Internet.verifyCredentials.getScreenName();
            screenName.setText(screenNameText);

            new UserTimeLineAsync(context).execute((Void)null);
        }
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

    private class UserTimeLineAsync extends AsyncTask<Void,Void,Void>{

        List<CommonStatusClass> statuses = new ArrayList<>();
        List<CommonStatusClass> homeStatuses = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        Context context;

        public UserTimeLineAsync(Context ctx){
            context = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Call<List<CommonStatusClass>> callStatuses =
                        Internet.service.getStatuses(Internet.verifyCredentials.getScreenName(),2);
                statuses = callStatuses.execute().body();

                Call<List<CommonStatusClass>> callHomeStatuses =
                        Internet.service.getHomeTimeline(2);
                //objects = callHomeStatuses.execute().body();
                homeStatuses = callHomeStatuses.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(statuses != null){
                TweetsViewAdapter adapter = new TweetsViewAdapter(context, statuses);
                usersTweets.setAdapter(adapter);
            }
        }
    }
}
