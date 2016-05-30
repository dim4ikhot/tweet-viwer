package com.dimka.twitt_reader.pojo_classes.status;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class ExtendedEntities {

    @SerializedName("media")
    @Expose
    private List<Medium__> media = new ArrayList<Medium__>();

    /**
     *
     * @return
     * The media
     */
    public List<Medium__> getMedia() {
        return media;
    }

    /**
     *
     * @param media
     * The media
     */
    public void setMedia(List<Medium__> media) {
        this.media = media;
    }

}