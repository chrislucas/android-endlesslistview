package imagineapps.testrequestafterscroll.entitiies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by r028367 on 10/07/2017.
 */

public class Token {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    public Token(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }
}
