package ua.tweets_reader;

import ua.tweets_reader.gson.current_user.User;
import ua.tweets_reader.gson.status.CommonStatusClass;
import ua.tweets_reader.gson.credentials.VerifyCredentials;
import ua.tweets_reader.retrofit.TwitterRest;

import retrofit2.Retrofit;

/*
 * Created by Dimka on 28.05.2016.
 */
public final class Internet {

    private Retrofit retrofit;
    private TwitterRest service;
    private VerifyCredentials verifyCredentials;
    private User currentUser;
    private User otherUser;
    private CommonStatusClass currentStatus;

    private static Internet instance;

    public static synchronized Internet getInstance(){
        if(instance == null){
            instance = new Internet();
        }
        return instance;
    }

    //Retrofit
    public void setRetrofit(Retrofit retrofit){
        this.retrofit = retrofit;
    }
    public Retrofit getRetrofit(){return retrofit;}

    //TwitterRest
    public void setTwitterRest(TwitterRest service){
        this.service = service;
    }
    public TwitterRest getTwitterRest(){return service;}

    //Credentials
    public void setCredentials(VerifyCredentials credentials){
        this.verifyCredentials = credentials;
    }
    public VerifyCredentials getCredentials(){return verifyCredentials;}

    //User
    public void setCurrentUser(User user){
        currentUser = user;
    }
    public User getCurrentUser(){return currentUser;}

    //User
    public void setOtherUser(User user){
        otherUser = user;
    }
    public User getOtherUser(){return otherUser;}

    //CommonStatusClass
    public void setCurrentStatus(CommonStatusClass status){
        currentStatus = status;
    }
    public CommonStatusClass getCurrentStatus(){return currentStatus;}

}
