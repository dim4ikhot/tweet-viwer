package com.dimka.twitt_reader.pojo_classes.status;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Url_ {

    @SerializedName("urls")
    @Expose
    private List<Url__> urls = new ArrayList<Url__>();

    /**
     *
     * @return
     * The urls
     */
    public List<Url__> getUrls() {
        return urls;
    }

    /**
     *
     * @param urls
     * The urls
     */
    public void setUrls(List<Url__> urls) {
        this.urls = urls;
    }

}
