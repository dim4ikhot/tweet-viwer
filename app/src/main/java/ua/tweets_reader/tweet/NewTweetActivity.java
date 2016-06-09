package ua.tweets_reader.tweet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ua.tweets_reader.Internet;
import ua.tweets_reader.MainActivity;
import com.dimka.twitt_reader.R;
import ua.tweets_reader.dialogs.InfoDialog;
import ua.tweets_reader.gson.status.NewStatusResult;

import retrofit2.Call;

public class NewTweetActivity extends AppCompatActivity {

    EditText newTweet;
    MenuItem charsCount;
    ActionBar bar;
    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newTweet = (EditText)findViewById(R.id.input_new_tweet);
        bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
        TextChangedListener();
        if(getIntent() != null) {
            String screenName = getIntent().getStringExtra(MainActivity.SCREEN_NAME);
            String quoteTweet = getIntent().getStringExtra(MainActivity.QUOTE_TEXT);
            if(screenName != null && !screenName.equals("")) {
                userName += "@" + screenName;
            }
            if(quoteTweet != null &&! quoteTweet.equals("")){
                if(!userName.equals("")){
                    userName = "\""+userName+ ": " + quoteTweet + "\"";
                }
                else {
                    userName += "\"" + quoteTweet + "\"";
                }
            }
        }
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
        newTweet.setText(userName);
        return true;
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

    public class SendNewTweet extends AsyncTask<String, Void, Boolean>{

        Context context;

        public SendNewTweet(Context ctx){
            context = ctx;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String text = params[0];
            Call<NewStatusResult> result =  Internet.getInstance().getTwitterRest().sendNewStatus(text);
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
                String message = getResources().getString(R.string.error_send_status_dialog_message);
                dlg.setTitle(message);
                dlg.show(NewTweetActivity.this.getSupportFragmentManager(), "error_dialog");
            }

        }
    }

}
