package com.dimka.twitt_reader.pojo_classes.verify_credentials;

/*
 * Created by Dimka on 26.05.2016.
 */
import javax.annotation.Generated;

import com.dimka.twitt_reader.pojo_classes.status.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Entities {

    @SerializedName("url")
    @Expose
    private Url_ url;
    @SerializedName("description")
    @Expose
    private Description description;

    /**
     *
     * @return
     * The url
     */
    public Url_ getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(Url_ url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The description
     */
    public Description getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(Description description) {
        this.description = description;
    }

}
