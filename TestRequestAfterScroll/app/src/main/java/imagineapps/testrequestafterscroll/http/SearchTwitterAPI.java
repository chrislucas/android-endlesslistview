package imagineapps.testrequestafterscroll.http;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import imagineapps.uptolv.utils.http.ModelHTTPRequest;

/**
 * Created by r028367 on 03/07/2017.
 */

public class SearchTwitterAPI extends ModelHTTPRequest {

    private String auth;

    public SearchTwitterAPI(String url, String auth) {
        super(url);
        this.auth = auth;
    }

    public SearchTwitterAPI(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    public static class ResponseTwitter implements Parcelable {

        public ResponseTwitter() {
        }

        public ResponseTwitter(Parcel in) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {}

        public static final Parcelable.Creator<ResponseTwitter> CREATOR = new Parcelable.Creator<ResponseTwitter>() {

            @Override
            public ResponseTwitter createFromParcel(Parcel source) {
                return new ResponseTwitter(source);
            }

            @Override
            public ResponseTwitter[] newArray(int size) {
                return new ResponseTwitter[size];
            }
        };
    }

    @Override
    public List<Parcelable> execute() {
        List<Parcelable> data = new ArrayList<>();
        try {
            String url = getUrl();
            HttpRequest request     = HttpRequest.get(url).authorization("Bearer" + auth);
            String response         = request.body();
            JSONObject jsonObject   = new JSONObject(response);
            JSONArray jsonArray     = jsonObject.getJSONArray("statuses");
        } catch (Exception e) {
            Log.e("EXCP_SEARCH_TWITTER", e.getMessage());
        }
        return data;
    }

    @Override
    public void afterExecution(List<Parcelable> data) {

    }
}
