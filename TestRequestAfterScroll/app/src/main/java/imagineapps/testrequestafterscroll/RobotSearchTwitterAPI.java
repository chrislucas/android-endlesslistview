package imagineapps.testrequestafterscroll;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * Created by r028367 on 03/07/2017.
 */

public class RobotSearchTwitterAPI implements Runnable {

    private String baseUrl = "https://api.twitter.com/1.1/search/tweets.json";
    private String queryString = "?q=@android";

    private String accessToken;

    public RobotSearchTwitterAPI(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void run() {
        if(isConnected()) {
            try {
                HttpRequest httpRequest = HttpRequest
                        .get(baseUrl + queryString)
                        .authorization("Bearer " + accessToken);
                String response = httpRequest.body();
            } catch (Exception e) {
                Log.e("EXCEPTION_SERVICE", e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return false;
    }
}
