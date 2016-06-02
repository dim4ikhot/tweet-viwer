package com.dimka.twitt_reader.pojo_classes.verify_credentials;

/*
 * Created by Dimka on 26.05.2016.
 */
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

import com.dimka.twitt_reader.pojo_classes.status.Medium;
import com.dimka.twitt_reader.pojo_classes.status.Url;
import com.dimka.twitt_reader.pojo_classes.status.UserMention;
import com.dimka.twitt_reader.pojo_classes.timeline.user_timeline.Hashtag;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Entities_ {

    @SerializedName("hashtags") //correct
    @Expose
    private List<Hashtag> hashtags = new ArrayList<>();

    @SerializedName("symbols")//correct
    @Expose
    private List<Object> symbols = new ArrayList<Object>();

    @SerializedName("user_mentions")//correct
    @Expose
    private List<UserMention> userMentions = new ArrayList<>();

    @SerializedName("urls")//correct
    @Expose
    //private List<Url> urls = new ArrayList<Url>();
    private List<Url> urls = new ArrayList<>();

    @SerializedName("media")
    @Expose
    private List<Medium> media = new ArrayList<Medium>();

    /**
     *
     * @return
     * The hashtags
     */
    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    /**
     *
     * @param hashtags
     * The hashtags
     */
    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    /**
     *
     * @return
     * The symbols
     */
    public List<Object> getSymbols() {
        return symbols;
    }

    /**
     *
     * @param symbols
     * The symbols
     */
    public void setSymbols(List<Object> symbols) {
        this.symbols = symbols;
    }

    /**
     *
     * @return
     * The userMentions
     */
    public List<UserMention> getUserMentions() {
        return userMentions;
    }

    /**
     *
     * @param userMentions
     * The user_mentions
     */
    public void setUserMentions(List<UserMention> userMentions) {
        this.userMentions = userMentions;
    }

    /**
     *
     * @return
     * The urls
     */
    public List<Url> getUrls() {
        return urls;
    }

    /**
     *
     * @param urls
     * The urls
     */
    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }


    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia(List<Medium> media) {
        this.media = media;
    }


}
