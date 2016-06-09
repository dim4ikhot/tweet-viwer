package ua.tweets_reader.gson.account_settings;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class AccountSettings {

    @SerializedName("protected")
    @Expose
    private Boolean _protected;
    @SerializedName("screen_name")
    @Expose
    private String screenName;
    @SerializedName("always_use_https")
    @Expose
    private Boolean alwaysUseHttps;
    @SerializedName("use_cookie_personalization")
    @Expose
    private Boolean useCookiePersonalization;
    @SerializedName("sleep_time")
    @Expose
    private SleepTime sleepTime;
    @SerializedName("geo_enabled")
    @Expose
    private Boolean geoEnabled;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("discoverable_by_email")
    @Expose
    private Boolean discoverableByEmail;
    @SerializedName("discoverable_by_mobile_phone")
    @Expose
    private Boolean discoverableByMobilePhone;
    @SerializedName("display_sensitive_media")
    @Expose
    private Boolean displaySensitiveMedia;
    @SerializedName("allow_contributor_request")
    @Expose
    private String allowContributorRequest;
    @SerializedName("allow_dms_from")
    @Expose
    private String allowDmsFrom;
    @SerializedName("allow_dm_groups_from")
    @Expose
    private String allowDmGroupsFrom;
    @SerializedName("smart_mute")
    @Expose
    private Boolean smartMute;

    /**
     *
     * @return
     * The _protected
     */
    public Boolean getProtected() {
        return _protected;
    }

    /**
     *
     * @param _protected
     * The protected
     */
    public void setProtected(Boolean _protected) {
        this._protected = _protected;
    }

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
     * The alwaysUseHttps
     */
    public Boolean getAlwaysUseHttps() {
        return alwaysUseHttps;
    }

    /**
     *
     * @param alwaysUseHttps
     * The always_use_https
     */
    public void setAlwaysUseHttps(Boolean alwaysUseHttps) {
        this.alwaysUseHttps = alwaysUseHttps;
    }

    /**
     *
     * @return
     * The useCookiePersonalization
     */
    public Boolean getUseCookiePersonalization() {
        return useCookiePersonalization;
    }

    /**
     *
     * @param useCookiePersonalization
     * The use_cookie_personalization
     */
    public void setUseCookiePersonalization(Boolean useCookiePersonalization) {
        this.useCookiePersonalization = useCookiePersonalization;
    }

    /**
     *
     * @return
     * The sleepTime
     */
    public SleepTime getSleepTime() {
        return sleepTime;
    }

    /**
     *
     * @param sleepTime
     * The sleep_time
     */
    public void setSleepTime(SleepTime sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     *
     * @return
     * The geoEnabled
     */
    public Boolean getGeoEnabled() {
        return geoEnabled;
    }

    /**
     *
     * @param geoEnabled
     * The geo_enabled
     */
    public void setGeoEnabled(Boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
    }

    /**
     *
     * @return
     * The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The discoverableByEmail
     */
    public Boolean getDiscoverableByEmail() {
        return discoverableByEmail;
    }

    /**
     *
     * @param discoverableByEmail
     * The discoverable_by_email
     */
    public void setDiscoverableByEmail(Boolean discoverableByEmail) {
        this.discoverableByEmail = discoverableByEmail;
    }

    /**
     *
     * @return
     * The discoverableByMobilePhone
     */
    public Boolean getDiscoverableByMobilePhone() {
        return discoverableByMobilePhone;
    }

    /**
     *
     * @param discoverableByMobilePhone
     * The discoverable_by_mobile_phone
     */
    public void setDiscoverableByMobilePhone(Boolean discoverableByMobilePhone) {
        this.discoverableByMobilePhone = discoverableByMobilePhone;
    }

    /**
     *
     * @return
     * The displaySensitiveMedia
     */
    public Boolean getDisplaySensitiveMedia() {
        return displaySensitiveMedia;
    }

    /**
     *
     * @param displaySensitiveMedia
     * The display_sensitive_media
     */
    public void setDisplaySensitiveMedia(Boolean displaySensitiveMedia) {
        this.displaySensitiveMedia = displaySensitiveMedia;
    }

    /**
     *
     * @return
     * The allowContributorRequest
     */
    public String getAllowContributorRequest() {
        return allowContributorRequest;
    }

    /**
     *
     * @param allowContributorRequest
     * The allow_contributor_request
     */
    public void setAllowContributorRequest(String allowContributorRequest) {
        this.allowContributorRequest = allowContributorRequest;
    }

    /**
     *
     * @return
     * The allowDmsFrom
     */
    public String getAllowDmsFrom() {
        return allowDmsFrom;
    }

    /**
     *
     * @param allowDmsFrom
     * The allow_dms_from
     */
    public void setAllowDmsFrom(String allowDmsFrom) {
        this.allowDmsFrom = allowDmsFrom;
    }

    /**
     *
     * @return
     * The allowDmGroupsFrom
     */
    public String getAllowDmGroupsFrom() {
        return allowDmGroupsFrom;
    }

    /**
     *
     * @param allowDmGroupsFrom
     * The allow_dm_groups_from
     */
    public void setAllowDmGroupsFrom(String allowDmGroupsFrom) {
        this.allowDmGroupsFrom = allowDmGroupsFrom;
    }

    /**
     *
     * @return
     * The smartMute
     */
    public Boolean getSmartMute() {
        return smartMute;
    }

    /**
     *
     * @param smartMute
     * The smart_mute
     */
    public void setSmartMute(Boolean smartMute) {
        this.smartMute = smartMute;
    }

}