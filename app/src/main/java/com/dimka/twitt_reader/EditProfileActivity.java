package com.dimka.twitt_reader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimka.twitt_reader.pojo_classes.current_user.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;

import retrofit2.Call;

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
                    new AplplyProfileChanges().execute(editName.getText().toString(),
                            editWebSite.getText().toString(),
                            editPlace.getText().toString(),
                            editAvouMyself.getText().toString());
                }
            });
        }
    }

    private void initControls(){
        photo = (ImageView)findViewById(R.id.image_logo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        background = (ImageView)findViewById(R.id.image_background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        editName = (EditText)findViewById(R.id.item_name);
        editPlace = (EditText)findViewById(R.id.item_place);
        editWebSite = (EditText)findViewById(R.id.item_web_site);
        editAvouMyself = (EditText)findViewById(R.id.item_about);
        fillProfileInfo();
    }

    public class AplplyProfileChanges extends AsyncTask<String,Void,Void>{
        Call<User> userCall;
        File imgPhoto;

        @Override
        public void onPreExecute(){
            imgPhoto = (File)photo.getTag();
        }

        @Override
        protected Void doInBackground(String... params) {
            try{
                userCall = Internet.service.updateProfile(params[0],params[1],params[2],params[3]);
                Internet.currentUser = userCall.execute().body();
                userCall = Internet.service.updateProfileImage(imageToBase64String(imgPhoto));
                Internet.currentUser = userCall.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            setResult(RESULT_OK);
            finish();
        }

        private String imageToBase64String(File f) throws Exception{
            long fileSize = f.length();
            byte[] imgInBytes = new byte[(int)fileSize];
            FileInputStream fis = new FileInputStream(f);
            fis.read(imgInBytes,0,(int)fileSize);
            fis.close();
            return Base64.encodeToString(imgInBytes, Base64.DEFAULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
            Uri uri = null;
            File f = null;
            long size = 0;
            if (data != null) {
                uri = data.getData();
                f = new File(getRealPathFromURI(this,uri));
                size = f.length();
            }
            if (uri != null) {
                if(size <= 700000) {
                    if (requestCode == 0) {
                        photo.setImageURI(uri);
                        photo.setTag(f);
                    }
                    else if (requestCode == 1) {
                        background.setImageURI(uri);
                        background.setTag(f);
                    }
                }
                else{
                    Toast.makeText(this,"You can load only files less then 700 Kb.", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void fillProfileInfo(){
        editName.setText(Internet.currentUser.getName());
        editPlace.setText(Internet.currentUser.getLocation());
        if(Internet.currentUser.getEntities().getUrl() != null) {
            editWebSite.setText(Internet.currentUser.getEntities().getUrl().getUrls().get(0).getDisplayUrl());
        }
        editAvouMyself.setText(Internet.currentUser.getDescription());
        new ImageLoader(this,photo,background).execute(Internet.currentUser.getProfileImageUrlHttps(),
                Internet.currentUser.getProfileBackgroundImageUrlHttps());
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
