package imagineapps.testrequestafterscroll.entitiies;

/**
 * Created by r028367 on 10/07/2017.
 */

public class Token {
    public String access_token;
    public Token(String access_token) {
        this.access_token = access_token;
    }
    public String getAccessToken() {
        return access_token;
    }
}
