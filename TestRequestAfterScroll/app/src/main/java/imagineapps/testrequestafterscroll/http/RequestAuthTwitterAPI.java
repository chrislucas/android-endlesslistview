package imagineapps.testrequestafterscroll.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import imagineapps.testrequestafterscroll.MainActivity;
import imagineapps.uptolv.utils.http.ModelHTTPRequest;

/**
 * Created by r028367 on 03/07/2017.
 */

public class RequestAuthTwitterAPI extends ModelHTTPRequest {

    private static final String KEY     = "lrCqswUocOPi6FAgGljnUdSZK";
    private static final String SECRET  = "oTjwPTCJZjljcyIfYDMMwFyB7W0FbcrY5jxMO2JvU6nnA6OSyE";

    private Handler handler;

    public RequestAuthTwitterAPI(String url) {
        super(url);
    }

    public RequestAuthTwitterAPI(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    public RequestAuthTwitterAPI(Handler handler, String url, Map<String, String> parameters) {
        super(url, parameters);
        this.handler = handler;
    }

    public static class AuthTwitter implements Parcelable {
        private JSONObject jsonObject;
        public AuthTwitter() {}
        public AuthTwitter(Parcel in) {
            jsonObject = (JSONObject) in.readValue(JSONObject.class.getClassLoader());
        }

        public JSONObject getJsonObject() {
            return jsonObject;
        }

        public void setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(jsonObject);
        }

        public static Parcelable.Creator<AuthTwitter> CREATOR = new Parcelable.Creator() {

            @Override
            public AuthTwitter createFromParcel(Parcel source) {
                return new AuthTwitter(source);
            }

            @Override
            public AuthTwitter[] newArray(int size) {
                return new AuthTwitter[size];
            }
        };
    }

    @Override
    public List<Parcelable> execute() {
        List<Parcelable> data = new ArrayList<>();
        Log.i("REQUEST_TWITTER_AUTH", "DO_REQUEST");
        try {
            String url = getUrl();
            Map<String, String> params = getParameters();
            HttpRequest request = HttpRequest.post(url);
            String response = request
                    .authorization("Basic " + generate(KEY, SECRET))
                    .form(params)
                    .body();
            JSONObject jsonObject   = new JSONObject(response);
            AuthTwitter authTwitter = new AuthTwitter();
            authTwitter.setJsonObject(jsonObject);
            data.add(authTwitter);
        }
        catch (Exception e) {
            Log.e("EXCP_REQUEST_AUTH", e.getMessage());
        }
        return data;
    }

    private String generate(String key, String value) {
        String token    = String.format("%s:%s", key, value);
        String base64   = Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
        return base64;
    }

    @Override
    public void afterExecution(List<Parcelable> data) {
        if(handler != null) {
            Message message = new Message();
            message.what    = MainActivity.HANDLER_MSG_AUTH_TWITTER;
            Bundle bundle   = new Bundle();
            ArrayList list  = (ArrayList<? extends Parcelable>) data;
            bundle.putParcelableArrayList(MainActivity.BUNDLE_DATA_ARRAYLIST_API, list);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
