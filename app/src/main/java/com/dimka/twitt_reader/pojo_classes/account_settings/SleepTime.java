package com.dimka.twitt_reader.pojo_classes.account_settings;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class SleepTime {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("end_time")
    @Expose
    private Object endTime;
    @SerializedName("start_time")
    @Expose
    private Object startTime;

    /**
     *
     * @return
     * The enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     *
     * @param enabled
     * The enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     *
     * @return
     * The endTime
     */
    public Object getEndTime() {
        return endTime;
    }

    /**
     *
     * @param endTime
     * The end_time
     */
    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    /**
     *
     * @return
     * The startTime
     */
    public Object getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime
     * The start_time
     */
    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

}

