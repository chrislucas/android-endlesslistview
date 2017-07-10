package imagineapps.testrequestafterscroll.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;

import imagineapps.testrequestafterscroll.entitiies.Info;
import imagineapps.testrequestafterscroll.http.BgDownloadBitmap;
import imagineapps.uptolv.action.DoAsyncTasks;

public class ServiceDownloadBitmap extends Service {
    private IBinder iBinder = new LocalBinder();

    private Handler handler;
    private List<Info> infoList;

    public ServiceDownloadBitmap() {}


    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public IBinder onBind(Intent intent) {
        initialize(intent);
        return iBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        initialize(intent);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void initialize(Intent intent) {
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                infoList = bundle.getParcelableArrayList(BUNDLE_INFO_LIST);
            }
        }
    }

    public class LocalBinder extends Binder {
        public ServiceDownloadBitmap getInstance() {
            return ServiceDownloadBitmap.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String BUNDLE_INFO_LIST = "BUNDLE_INFO_LIST";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                infoList = bundle.getParcelableArrayList(BUNDLE_INFO_LIST);
            }
        }
        return START_NOT_STICKY;
    }

    public void doRequest() {
        BgDownloadBitmap bgDownloadBitmap = new BgDownloadBitmap(infoList, handler, this);
        DoAsyncTasks doAsyncTasks         = new DoAsyncTasks(bgDownloadBitmap);
        doAsyncTasks.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
