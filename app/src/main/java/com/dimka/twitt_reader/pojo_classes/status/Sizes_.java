package com.dimka.twitt_reader.pojo_classes.status;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Sizes_ {

    @SerializedName("thumb")
    @Expose
    private Thumb_ thumb;
    @SerializedName("large")
    @Expose
    private Large_ large;
    @SerializedName("medium")
    @Expose
    private Medium___ medium;
    @SerializedName("small")
    @Expose
    private Small_ small;

    /**
     *
     * @return
     * The thumb
     */
    public Thumb_ getThumb() {
        return thumb;
    }

    /**
     *
     * @param thumb
     * The thumb
     */
    public void setThumb(Thumb_ thumb) {
        this.thumb = thumb;
    }

    /**
     *
     * @return
     * The large
     */
    public Large_ getLarge() {
        return large;
    }

    /**
     *
     * @param large
     * The large
     */
    public void setLarge(Large_ large) {
        this.large = large;
    }

    /**
     *
     * @return
     * The medium
     */
    public Medium___ getMedium() {
        return medium;
    }

    /**
     *
     * @param medium
     * The medium
     */
    public void setMedium(Medium___ medium) {
        this.medium = medium;
    }

    /**
     *
     * @return
     * The small
     */
    public Small_ getSmall() {
        return small;
    }

    /**
     *
     * @param small
     * The small
     */
    public void setSmall(Small_ small) {
        this.small = small;
    }

}