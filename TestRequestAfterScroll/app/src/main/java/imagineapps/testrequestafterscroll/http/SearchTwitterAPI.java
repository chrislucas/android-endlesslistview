package imagineapps.testrequestafterscroll.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import imagineapps.testrequestafterscroll.MainActivity;
import imagineapps.testrequestafterscroll.utils.UtilsSimpleFormatDate;
import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.uptolv.utils.http.ModelHTTPRequest;

/**
 * Created by r028367 on 03/07/2017.
 */

public class SearchTwitterAPI extends ModelHTTPRequest {

    private Handler handler;
    private String auth;

    public SearchTwitterAPI(String url, String auth, Handler handler) {
        super(url);
        this.auth       = auth;
        this.handler    = handler;
    }

    public SearchTwitterAPI(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    public static class ResponseTwitter implements Parcelable {
        public ResponseTwitter() {}
        public ResponseTwitter(Parcel in) {}

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
            HttpRequest request = HttpRequest
                    .get(url)
                    .authorization("Bearer " + auth);
            String response         = request.body();
            JSONObject jsonObject   = new JSONObject(response);
            if(!jsonObject.has("errors")) {
                if(jsonObject.has("statuses")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("statuses");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject tweetInfo = jsonArray.getJSONObject(i);
                        Info info = new Info();
                        String id = tweetInfo.getString("id");
                        info.setId(id);
                        info.setText(tweetInfo.getString("text"));
                        String createAt = tweetInfo.getString("created_at");
                        long time       = UtilsSimpleFormatDate.convertUTCToMilliseconds(createAt, "EEE MMM d HH:mm:ss Z yyyy");
                        info.setDate(time);
                        //String dateFmt  = UtilsSimpleFormatDate.convertLongToDateFormat(time);
                        JSONObject jsonUser         = tweetInfo.getJSONObject("user");
                        //String urlImageUser       = jsonUser.getString("profile_image_url");
                        String urlImageBackground   = jsonUser.getString("profile_background_image_url");
                        String userName             = jsonUser.getString("name");
                        info.setTitle(userName);
                        info.setUrlImage(urlImageBackground);
                        data.add(info);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("EXCP_SEARCH_TWITTER", e.getMessage());
        }
        return data;
    }

    @Override
    public void afterExecution(List<Parcelable> data) {
        if(handler != null) {
            Message message = new Message();
            message.what    = MainActivity.HANDLER_MSG_TWITTER_SEARCH;
            Bundle bundle   = new Bundle();
            ArrayList list  = (ArrayList<? extends Parcelable>) data;
            bundle.putParcelableArrayList(MainActivity.BUNDLE_DATA_ARRAYLIST_API, list);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
