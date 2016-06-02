package com.dimka.twitt_reader;

import com.dimka.twitt_reader.pojo_classes.current_user.User;
import com.dimka.twitt_reader.pojo_classes.status.CommonStatusClass;
import com.dimka.twitt_reader.pojo_classes.verify_credentials.VerifyCredentials;
import com.dimka.twitt_reader.rest_api_retrofit_interface.TwitterRest;

import retrofit2.Retrofit;

/*
 * Created by Dimka on 28.05.2016.
 */
public class Internet {

    public static Retrofit retrofit;
    public static TwitterRest service;
    public static VerifyCredentials verifyCredentials;
    public static User currentUser;
    public static CommonStatusClass currentStatus;
}
