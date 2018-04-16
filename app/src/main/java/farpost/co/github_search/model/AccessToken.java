package farpost.co.github_search.model;


import com.google.gson.annotations.SerializedName;

public class AccessToken {

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        return tokenType;
    }

}
