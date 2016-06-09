package ua.tweets_reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import ua.tweets_reader.dialogs.RetweetDialog;
import ua.tweets_reader.adapters.TweetsViewAdapter;
import ua.tweets_reader.gson.current_user.User;
import ua.tweets_reader.gson.status.CommonStatusClass;
import ua.tweets_reader.tweet.NewTweetActivity;

import com.dimka.twitt_reader.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class ViewProfileActivity extends AppCompatActivity implements TweetsViewAdapter.onAnswerClickListener,
        RetweetDialog.onButtonsClickListener {

    public static final int PROFILE_EDIT = 0;
    public static final int VIEW_STATUS = 1;

    ImageView headerImage, profileImage;
    Button editProfile;
    ImageButton addNewUser;
    TextView readingCount, readersCount, fullName, screenName, aboutMySelf, place, site;
    ListView usersTweets;
    ActionBar bar;
    User user;
    boolean isShowMyself;
    TweetsViewAdapter adapter;
    LinearLayout profileProgress, profileMain;
    Internet internetParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
        internetParams = Internet.getInstance();
        addNewUser = (ImageButton) findViewById(R.id.button_add_user);
        addNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateDestroyFriendship().execute(user.getFollowing());
            }
        });
        isShowMyself = getIntent().getBooleanExtra("isShowMySelf", true);
        if (isShowMyself) {
            if (internetParams.getCurrentUser() != null) {
                user = internetParams.getCurrentUser();
                addNewUser.setVisibility(View.GONE);
            }
        } else {
            if (internetParams.getOtherUser() != null) {
                user = internetParams.getOtherUser();
                addNewUser.setVisibility(View.VISIBLE);
            }
        }
        initControls();
    }

    @Override
    public void onAnswerClick(CommonStatusClass status) {
        Intent intent = new Intent(this, NewTweetActivity.class);
        intent.putExtra(MainActivity.SCREEN_NAME,
                status.getUser().getScreenName());
        startActivityForResult(intent, MainActivity.NEW_TWEET);
    }

    @Override
    public void onRetweetClick(CommonStatusClass status) {
        RetweetDialog dlg = new RetweetDialog();
        internetParams.setCurrentStatus(status);
        dlg.show(getSupportFragmentManager(), "questiobDialog");
    }

    @Override
    public void onFavoriteClick() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onQuoteButtonClick() {
        Intent intent = new Intent(this, NewTweetActivity.class);
        intent.putExtra(MainActivity.QUOTE_TEXT,
                internetParams.getCurrentStatus().getText());
        intent.putExtra(MainActivity.SCREEN_NAME,
                internetParams.getCurrentStatus().getUser().getScreenName());
        startActivityForResult(intent, MainActivity.NEW_TWEET);
    }

    @Override
    public void onRetweetButtonClick() {
        CommonStatusClass currentStatus = internetParams.getCurrentStatus();
        boolean isRetweeted = !currentStatus.getRetweeted();
        int retweetCount = currentStatus.getRetweetCount();
        if (isRetweeted) {
            currentStatus.setRetweetCount(retweetCount + 1);
        } else {
            currentStatus.setRetweetCount(retweetCount - 1);
        }
        currentStatus.setRetweeted(isRetweeted);
        new RetweetUnRetweeAsync(!isRetweeted, adapter).execute(currentStatus.getId());
    }

    private void initControls() {
        profileProgress = (LinearLayout) findViewById(R.id.profile_progress);
        profileMain = (LinearLayout) findViewById(R.id.profileMainScreen);
        headerImage = (ImageView) findViewById(R.id.image_header);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        editProfile = (Button) findViewById(R.id.button_edit_profile);
        readingCount = (TextView) findViewById(R.id.profile_reading_count_value);
        readersCount = (TextView) findViewById(R.id.profile_readers_count_value);
        fullName = (TextView) findViewById(R.id.profile_full_name);
        screenName = (TextView) findViewById(R.id.profile_screen_name);
        usersTweets = (ListView) findViewById(R.id.usersTweets);
        usersTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonStatusClass status = (CommonStatusClass) view.getTag();
                if (status != null) {
                    internetParams.setCurrentStatus(status);
                    startActivityForResult(new Intent(ViewProfileActivity.this, ReviewCurrentTweet.class), VIEW_STATUS);
                }
            }
        });
        //  scrollTest = (ScrollView)findViewById(R.id.scrolltest);
        // setListViewHeightBasedOnChildren(usersTweets,scrollTest);
        aboutMySelf = (TextView) findViewById(R.id.profile_about_myself);
        place = (TextView) findViewById(R.id.profile_place);
        site = (TextView) findViewById(R.id.profile_site);
        if (!isShowMyself) {
            editProfile.setVisibility(View.GONE);
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ViewProfileActivity.this, EditProfileActivity.class), PROFILE_EDIT);
            }
        });
        fillsControl();
    }

    private void fillsControl() {
        profileProgress.setVisibility(View.VISIBLE);
        profileMain.setVisibility(View.GONE);
        new LoadProfileParams(this).execute((Void) null);
    }

    private class LoadProfileParams extends AsyncTask<Void, Void, Void> {
        Context context;
        Bitmap bmpBackground, bmpProfileIcon;
        List<CommonStatusClass> statuses = new ArrayList<>();

        public LoadProfileParams(Context ctx) {
            context = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (user != null) {
                String backgroundURL = user.getProfileBackgroundImageUrlHttps();
                String profileImage = user.getProfileImageUrlHttps();
                Call<List<CommonStatusClass>> callStatuses =
                        internetParams.getTwitterRest().getStatuses(user.getScreenName(), 100);
                try {
                    statuses = callStatuses.execute().body();
                    bmpBackground = Picasso.with(context).load(backgroundURL).get();
                    bmpProfileIcon = Picasso.with(context).load(profileImage).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (bmpBackground != null) {
                headerImage.setImageBitmap(bmpBackground);
            }
            if (bmpProfileIcon != null) {
                profileImage.setImageBitmap(bmpProfileIcon);
            }
            setButtonColor(context, user.getFollowing());

            String reading = "" + user.getFriendsCount();
            readingCount.setText(reading);

            String readers = "" + user.getFollowersCount();
            readersCount.setText(readers);

            fullName.setText(user.getName());

            String screenNameText = "@" + user.getScreenName();
            screenName.setText(screenNameText);

            if (!user.getDescription().equals("")) {
                aboutMySelf.setText(user.getDescription());
                aboutMySelf.setVisibility(View.VISIBLE);
            } else {
                aboutMySelf.setVisibility(View.GONE);
            }
            if (!user.getLocation().equals("")) {
                place.setText(user.getLocation());
                place.setVisibility(View.VISIBLE);
            } else {
                place.setVisibility(View.GONE);
            }
            if (user.getEntities().getUrl() != null) {
                site.setText(user.getEntities().getUrl().getUrls().get(0).getDisplayUrl());
                site.setVisibility(View.VISIBLE);
            } else {
                site.setVisibility(View.GONE);
            }

            if (statuses != null) {
                adapter = new TweetsViewAdapter(context, statuses, true);
                usersTweets.setAdapter(adapter);
                int to = MainActivity.getTweetPosition(context, statuses);
                if (to != -1) {
                    usersTweets.setSelection(to);
                }
            }

            profileProgress.setVisibility(View.GONE);
            profileMain.setVisibility(View.VISIBLE);
        }
    }


    private void setButtonColor(Context context, boolean isFolofing) {
        if (isFolofing) {
            addNewUser.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            addNewUser.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_person_ok));
        } else {
            addNewUser.setBackgroundColor(Color.WHITE);
            addNewUser.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_person_add));
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

    private class CreateDestroyFriendship extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean isFollowing;
            Call<User> friendshipCall;
            if (!params[0]) {
                friendshipCall = internetParams.getTwitterRest().createFriendship(user.getId());
                isFollowing = true;
            } else {
                friendshipCall = internetParams.getTwitterRest().destroyFriendship(user.getId());
                isFollowing = false;
            }
            try {
                friendshipCall.execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isFollowing;
        }

        @Override
        public void onPostExecute(Boolean result) {
            setButtonColor(ViewProfileActivity.this, result);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, ScrollView parent) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        parent.setLayoutParams(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //change name, images
        if (requestCode == PROFILE_EDIT || requestCode == VIEW_STATUS) {
            fillsControl();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
