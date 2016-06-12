package ua.tweetsreader.retrofit;

import ua.tweetsreader.gson.accountsettings.AccountSettings;
import ua.tweetsreader.gson.user.User;
import ua.tweetsreader.gson.status.NewStatusResult;
import ua.tweetsreader.gson.status.CommonStatusClass;
import ua.tweetsreader.gson.credentials.VerifyCredentials;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
 * Created by Dimka on 22.05.2016.
 */
public interface TwitterRest {

    String toQuery = "?oauth_version=1%2E0&oauth_consumer_key=fRuPxDVC1J58r05jdJIDn7qCJ&oauth_nonce=fc7a26742d8b307f3a94d98c498a56d8&oauth_signature=oek0fJOkahQzkzLp2lbkwLGu4tg%3D&oauth_signature_method=HMAC%2DSHA1&oauth_timestamp=1464203403&oauth_token=3996686661%2DZrhwq55zZpMV83YudVke38hm8oeECSNXrwwsati";

    @GET("account/settings.json")
    Call<AccountSettings> accauntSettings();

    @GET("account/verify_credentials.json")
    Call<VerifyCredentials> getVerifyCredentials();

    @GET("users/show.json")
    Call<User> getUser(@Query("screen_name") String screenNAme);

    @POST("statuses/update.json")
    Call<NewStatusResult> sendNewStatus(@Query("status") String textStatus);

    @GET("statuses/user_timeline.json")
    Call<List<CommonStatusClass>> getStatuses(@Query("screen_name") String screenName, @Query("count") int count);

    @GET("statuses/home_timeline.json")
    Call<List<CommonStatusClass>> getHomeTimeline(@Query("count") int count);

    @POST("account/update_profile.json")
    Call<User> updateProfile(@Query("name") String name,
                               @Query("url") String url,
                               @Query("location") String location,
                               @Query("description") String description);


    @POST("statuses/retweet/{id}.json")
    Call<CommonStatusClass> retweetStatus(@Path("id") long tweet);


    @POST("statuses/unretweet/{id}.json")
    Call<CommonStatusClass> unretweetStatus(@Path("id") long tweet);


    @POST("favorites/create.json")
    Call<CommonStatusClass> createFavorite(@Query("id") long id, @Query("include_entities") boolean includeEntities);

    @POST("favorites/destroy.json")
    Call<CommonStatusClass> destroyFavorite(@Query("id") long id, @Query("include_entities") boolean includeEntities);

    @POST("statuses/destroy/{id}.json")
    Call<CommonStatusClass> destroyStatus(@Path("id") long statusId);

    @GET("users/search.json")
    Call<List<User>> searchUsers(@Query("q") String request);

    @POST("friendships/create.json")
    Call<User> createFriendship(@Query("user_id") long userId);

    @POST("friendships/destroy.json")
    Call<User> destroyFriendship(@Query("user_id") long userId);



    @Multipart
    @POST("account/update_profile_image.json")
    Call<Object> updateProfileImage(@Part MultipartBody.Part file, @Query("image")String base64image);

}
