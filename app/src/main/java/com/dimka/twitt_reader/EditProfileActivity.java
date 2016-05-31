package com.dimka.twitt_reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    ActionBar bar;
    ImageView photo,background;
    EditText editName,editPlace,editWebSite,editAvouMyself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle("Cancel");
        }
        initControls();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Apply changes
                }
            });
        }
    }

    private void initControls(){
        photo = (ImageView)findViewById(R.id.image_logo);
        background = (ImageView)findViewById(R.id.image_background);
        editName = (EditText)findViewById(R.id.item_name);
        editPlace = (EditText)findViewById(R.id.item_place);
        editWebSite = (EditText)findViewById(R.id.item_web_site);
        editAvouMyself = (EditText)findViewById(R.id.item_about);
        fillProfileInfo();
    }

    private void fillProfileInfo(){
        editName.setText(Internet.verifyCredentials.getName());
        editPlace.setText(Internet.verifyCredentials.getLocation());
        editWebSite.setText(Internet.verifyCredentials.getEntities().getUrl().getUrls().get(0).getDisplayUrl());
        editAvouMyself.setText(Internet.verifyCredentials.getDescription());
        new ImageLoader(this,photo,background).execute(Internet.verifyCredentials.getProfileImageUrlHttps(),
                Internet.verifyCredentials.getProfileBackgroundImageUrlHttps());
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImageLoader extends AsyncTask<String,Void,Void>{

        ImageView logo;
        ImageView backgound;
        Context context;
        Bitmap bmpLogo,bmpBackground;

        public ImageLoader(Context ctx, ImageView logo, ImageView backgound){
            this.logo = logo;
            this.backgound = backgound;
            context = ctx;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                //0 - logo image
                bmpLogo = Picasso.with(context).load(params[0]).get();
                //1 - background image
                bmpBackground = Picasso.with(context).load(params[1]).get();
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        public void onPostExecute(Void result){
            logo.setImageBitmap(bmpLogo);
            backgound.setImageBitmap(bmpBackground);
        }
    }

}
