package ua.tweetsreader.gson.timeline;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Hashtag {

    @SerializedName("text")//correct
    @Expose
    private String text;

    @SerializedName("indices")//corect
    @Expose
    private List<Integer> indices = new ArrayList<>();

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
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
