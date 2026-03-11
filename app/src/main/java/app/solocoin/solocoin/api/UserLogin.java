package app.solocoin.solocoin.api;

import com.google.gson.annotations.SerializedName;

public class UserLogin {
    @SerializedName("id_token")
    private String id_token;
    @SerializedName("uid")
    private String uid;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("country_code")
    private String cc;

    public UserLogin(String id_token, String uid, String mobile, String cc) {
        this.id_token = id_token;
        this.uid = uid;
        this.mobile = mobile;
        this.cc = cc;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }
}
