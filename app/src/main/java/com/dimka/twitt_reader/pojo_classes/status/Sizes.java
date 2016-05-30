package com.dimka.twitt_reader.pojo_classes.status;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Sizes {

    @SerializedName("thumb")
    @Expose
    private Thumb thumb;
    @SerializedName("large")
    @Expose
    private Large large;
    @SerializedName("medium")
    @Expose
    private Medium_ medium;
    @SerializedName("small")
    @Expose
    private Small small;

    /**
     *
     * @return
     * The thumb
     */
    public Thumb getThumb() {
        return thumb;
    }

    /**
     *
     * @param thumb
     * The thumb
     */
    public void setThumb(Thumb thumb) {
        this.thumb = thumb;
    }

    /**
     *
     * @return
     * The large
     */
    public Large getLarge() {
        return large;
    }

    /**
     *
     * @param large
     * The large
     */
    public void setLarge(Large large) {
        this.large = large;
    }

    /**
     *
     * @return
     * The medium
     */
    public Medium_ getMedium() {
        return medium;
    }

    /**
     *
     * @param medium
     * The medium
     */
    public void setMedium(Medium_ medium) {
        this.medium = medium;
    }

    /**
     *
     * @return
     * The small
     */
    public Small getSmall() {
        return small;
    }

    /**
     *
     * @param small
     * The small
     */
    public void setSmall(Small small) {
        this.small = small;
    }

}
