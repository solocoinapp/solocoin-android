package app.solocoin.solocoin;

import com.google.gson.annotations.SerializedName;

public class SessionBody {
@SerializedName("type")
    private String type;

    public SessionBody(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
