package com.dimka.twitt_reader.pojo_classes.status;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class UserMention {

    @SerializedName("screen_name")//correct
    @Expose
    private String screenName;

    @SerializedName("name")//correct
    @Expose
    private String name;

    @SerializedName("id")//correct
    @Expose
    private long id;

    @SerializedName("id_str")//correct
    @Expose
    private String idStr;

    @SerializedName("indices") // correct
    @Expose
    private List<Integer> indices = new ArrayList<Integer>();

    /**
     *
     * @return
     * The screenName
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     *
     * @param screenName
     * The screen_name
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The idStr
     */
    public String getIdStr() {
        return idStr;
    }

    /**
     *
     * @param idStr
     * The id_str
     */
    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    /**
     *
     * @return
     * The indices
     */
    public List<Integer> getIndices() {
        return indices;
    }

    /**
     *
     * @param indices
     * The indices
     */
    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

}
