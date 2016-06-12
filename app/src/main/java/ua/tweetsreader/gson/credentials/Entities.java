package ua.tweetsreader.gson.credentials;

/*
 * Created by Dimka on 26.05.2016.
 */
import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ua.tweetsreader.gson.status.Url_;

@Generated("org.jsonschema2pojo")
public class Entities {


    @SerializedName("url")
    @Expose
    private Url_ url;


    @SerializedName("description")//correct
    @Expose
    private Description description;


    public Url_ getUrl() {
        return url;
    }

    public void setUrl(Url_ url) {
        this.url = url;
    }



    public Description getDescription() {
        return description;
    }
    public void setDescription(Description description) {
        this.description = description;
    }

}
