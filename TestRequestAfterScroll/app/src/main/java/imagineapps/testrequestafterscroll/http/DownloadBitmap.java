package imagineapps.testrequestafterscroll.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import imagineapps.testrequestafterscroll.MainActivity;
import imagineapps.testrequestafterscroll.entitiy.Info;
import imagineapps.uptolv.utils.http.ModelHTTPRequest;

/**
 * Created by r028367 on 04/07/2017.
 */

public class DownloadBitmap extends ModelHTTPRequest {

    private Handler handler;
    private List<Info> listInfo;

    public DownloadBitmap(List<Info> listInfo, Handler handler) {
        this.listInfo = listInfo;
        this.handler  = handler;
    }

    public DownloadBitmap(String url, List<Info> listInfo, Handler handler) {
        super(url);
    }

    public DownloadBitmap(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    @Override
    public List<Parcelable> execute() {
        List<Parcelable> list = new ArrayList<>();
        for(Info info : listInfo) {
            try {
                String urlImage = info.getUrlImage();
            } catch (Exception e) {

            }
            list.add((Parcelable)info);
        }
        return list;
    }

    @Override
    public void afterExecution(List<Parcelable> data) {
        if(handler != null) {
            Message message = new Message();
            message.what    = MainActivity.HANDLER_MSG_DOWNLOAD_BITMAP;
            Bundle bundle   = new Bundle();
            ArrayList list  = (ArrayList<? extends Parcelable>) data;
            bundle.putParcelableArrayList(MainActivity.BUNDLE_DATA_ARRAYLIST_API, list);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}
