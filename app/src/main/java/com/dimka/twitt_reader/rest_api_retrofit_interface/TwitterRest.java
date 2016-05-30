package com.dimka.twitt_reader.rest_api_retrofit_interface;

import com.dimka.twitt_reader.pojo_classes.Error.Errors;
import com.dimka.twitt_reader.pojo_classes.account_settings.AccauntSettings;
import com.dimka.twitt_reader.pojo_classes.new_status.NewStatusResult;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.timeline.user_timeline.UserTimeline;
import com.dimka.twitt_reader.pojo_classes.verify_credentials.VerifyCredentials;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/*
 * Created by Dimka on 22.05.2016.
 */
public interface TwitterRest {

    String toQuery = "?oauth_version=1%2E0&oauth_consumer_key=fRuPxDVC1J58r05jdJIDn7qCJ&oauth_nonce=fc7a26742d8b307f3a94d98c498a56d8&oauth_signature=oek0fJOkahQzkzLp2lbkwLGu4tg%3D&oauth_signature_method=HMAC%2DSHA1&oauth_timestamp=1464203403&oauth_token=3996686661%2DZrhwq55zZpMV83YudVke38hm8oeECSNXrwwsati";

    @GET("account/settings.json")
    Call<AccauntSettings> accauntSettings();

    @GET("account/verify_credentials.json")
    Call<VerifyCredentials> getVerifyCredentials();

    @POST("statuses/update.json")
    Call<NewStatusResult> sendNewStatus(@Query("status") String textStatus);

    @GET("statuses/user_timeline.json")
    Call<List<UserTimeline>> getStatuses(@Query("count") int count);

    @GET("statuses/user_timeline.json")
    Call<List<UserTimeline>> getStatuses();

    @GET("statuses/user_timeline.json")
    Call<Errors> getStatusesError(@Query("count") int count);
}
