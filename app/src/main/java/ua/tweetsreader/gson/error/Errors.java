package ua.tweetsreader.gson.error;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Errors {

    @SerializedName("errors")
    @Expose
    private List<Error> errors = new ArrayList<Error>();

    /**
     *
     * @return
     * The errors
     */
    public List<Error> getErrors() {
        return errors;
    }

    /**
     *
     * @param errors
     * The errors
     */
    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

}
