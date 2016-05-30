package com.dimka.twitt_reader.tweet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.dimka.twitt_reader.Internet;
import com.dimka.twitt_reader.R;
import com.dimka.twitt_reader.dialogs.InfoDialog;
import com.dimka.twitt_reader.pojo_classes.new_status.NewStatusResult;

import retrofit2.Call;

public class NewTweetActivity extends AppCompatActivity {

    EditText newTweet;
    MenuItem charsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newTweet = (EditText)findViewById(R.id.input_new_tweet);
        TextChangedListener();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if ( fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SendNewTweet(NewTweetActivity.this).execute(newTweet.getText().toString());
                }
            });
        }
    }

    private void TextChangedListener(){
        newTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>140){
                    CharSequence maxText = s.subSequence(0,139);
                    s.clear();
                    s = s.replace(0, s.length(), maxText);
                }
                if(charsCount != null){
                    charsCount.setTitle("" + (140 - s.length()));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_tweet_menu, menu);
        charsCount = menu.findItem(R.id.chars_count);
        return true;
    }

    public class SendNewTweet extends AsyncTask<String, Void, Boolean>{

        Context context;

        public SendNewTweet(Context ctx){
            context = ctx;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Call<NewStatusResult> result =  Internet.service.sendNewStatus(params[0]);
            NewStatusResult statusResult = null;
            try {
                statusResult = result.execute().body();
            }catch(Exception e){
                e.printStackTrace();
            }
            return statusResult != null;
        }

        @Override
        protected  void onPostExecute(Boolean result){
            if(result){
                finish();
            }
            else{
                InfoDialog dlg = new InfoDialog();
                dlg.setTitle("Ooops. Something wrong...");
            }

        }
    }

}