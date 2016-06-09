package ua.tweets_reader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dimka.twitt_reader.R;

import ua.tweets_reader.gson.current_user.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SearchActivity extends AppCompatActivity {

    ImageButton searchButton;
    EditText searchEdit;
    ListView foundUsers;
    FoundUsersAdapter foundAdapter;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bar = getSupportActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        searchButton = (ImageButton) findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchEdit.getText().toString().equals("")) {
                    new CompleteSearch(SearchActivity.this).execute(searchEdit.getText().toString());
                }
            }
        });
        searchEdit = (EditText) findViewById(R.id.edit_search);
        foundUsers = (ListView) findViewById(R.id.list_found);
        foundUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Internet.getInstance().setOtherUser((User) view.getTag());
                startActivityForResult(
                        new Intent(SearchActivity.this,
                                ViewProfileActivity.class)
                                .putExtra("isShowMySelf", false), MainActivity.VIEW_PROFILE);
            }
        });
    }

    private class CompleteSearch extends AsyncTask<String, Void, Void> {
        List<User> usersList = new ArrayList<>();
        Context context;

        public CompleteSearch(Context ctx) {
            context = ctx;
        }

        @Override
        protected Void doInBackground(String... params) {
            Call<List<User>> userCall = Internet.getInstance().getTwitterRest().searchUsers(params[0]);
            try {
                usersList = userCall.execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            if (usersList.size() != 0) {
                foundAdapter = new FoundUsersAdapter(context, usersList);
                foundUsers.setAdapter(foundAdapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FoundUsersAdapter extends BaseAdapter {

        List<User> foundUsers;
        View v;
        Context context;
        LayoutInflater inflater;

        public FoundUsersAdapter(Context ctx, List<User> users) {
            foundUsers = users;
            context = ctx;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return foundUsers.size();
        }

        @Override
        public User getItem(int position) {
            return foundUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            v = convertView != null ? convertView
                    : inflater.inflate(R.layout.search_activity_list_item, parent, false);
            ImageView userLogo = (ImageView) v.findViewById(R.id.found_user_image);
            TextView userName = (TextView) v.findViewById(R.id.found_user_name);
            TextView userScreenName = (TextView) v.findViewById(R.id.found_user_screen_name);
            User currentUser = foundUsers.get(position);
            //load Image
            new ImageLoader(context, userLogo).execute(currentUser.getProfileImageUrlHttps());
            userName.setText(currentUser.getName());
            String correctUserName = "@" + currentUser.getScreenName();
            userScreenName.setText(correctUserName);
            v.setTag(currentUser);
            return v;
        }
    }
}
