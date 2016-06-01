package com.dimka.twitt_reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

    public static final int PROFILE_EDIT = 0;

    ImageView headerImage, profileImage;
    Button editProfile;
    TextView readingCount, readersCount, fullName, screenName, aboutMySelf, place, site;
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
        aboutMySelf = (TextView)findViewById(R.id.profile_about_myself);
        place = (TextView)findViewById(R.id.profile_place);
        site = (TextView)findViewById(R.id.profile_site);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ViewProfileActivity.this,EditProfileActivity.class), PROFILE_EDIT);
            }
        });
        fillsControl();
    }

    private void fillsControl(){
        new LoadProfileParams(this).execute((Void)null);
    }

    private class LoadProfileParams extends AsyncTask<Void,Void,Void>{
        Context context;
        Bitmap bmpBackground, bmpProfileIcon;
        List<CommonStatusClass> statuses = new ArrayList<>();

        public LoadProfileParams(Context ctx){
            context = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(Internet.currentUser != null){
                String backgroundURL = Internet.currentUser.getProfileBackgroundImageUrlHttps();
                String profileImage = Internet.currentUser.getProfileImageUrlHttps();
                Call<List<CommonStatusClass>> callStatuses =
                        Internet.service.getStatuses(
                                Internet.currentUser.getScreenName(),2);
                try {
                    statuses = callStatuses.execute().body();
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
            String reading = "" + Internet.currentUser.getFriendsCount();
            readingCount.setText(reading);
            String readers = "" + Internet.currentUser.getFollowersCount();
            readersCount.setText(readers);
            fullName.setText(Internet.currentUser.getName());
            String screenNameText = "@"+Internet.currentUser.getScreenName();
            screenName.setText(screenNameText);

            if(!Internet.currentUser.getDescription().equals("")) {
                aboutMySelf.setText(Internet.currentUser.getDescription());
                aboutMySelf.setVisibility(View.VISIBLE);
            }
            else{
                aboutMySelf.setVisibility(View.GONE);
            }
            if(!Internet.currentUser.getLocation().equals("")) {
                place.setText(Internet.currentUser.getLocation());
                place.setVisibility(View.VISIBLE);
            }
            else{
                place.setVisibility(View.GONE);
            }
            if(Internet.currentUser.getEntities().getUrl() != null) {
                site.setText(Internet.currentUser.getEntities().getUrl().getUrls().get(0).getDisplayUrl());
                site.setVisibility(View.VISIBLE);
            }
            else{
                site.setVisibility(View.GONE);
            }

            if(statuses != null){
                TweetsViewAdapter adapter = new TweetsViewAdapter(context, statuses);
                usersTweets.setAdapter(adapter);
            }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        //change name, images
        if(requestCode == PROFILE_EDIT) {
            fillsControl();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
