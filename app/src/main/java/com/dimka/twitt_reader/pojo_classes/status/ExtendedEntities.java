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
    private List<Medium> media = new ArrayList<>();

    /**
     *
     * @return
     * The media
     */
    public List<Medium> getMedia() {
        return media;
    }

    /**
     *
     * @param media
     * The media
     */
    public void setMedia(List<Medium> media) {
        this.media = media;
    }

}