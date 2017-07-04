package imagineapps.testrequestafterscroll.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import imagineapps.testrequestafterscroll.http.RequestAuthTwitterAPI;
import imagineapps.uptolv.action.DoOnBackground;
import imagineapps.uptolv.action.DoAsyncTasks;


/**
 * Ciclo
 * onCreate
 * onBind
 * */

public class ServiceAuthTwitter extends Service {

    private Handler handler;
    private final IBinder iBinder = new LocalBinder();

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ServiceAuthTwitter() {}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @see Service#onStartCommand
     */
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void doRequest() {
        String url = "https://api.twitter.com/oauth2/token";
        Map<String, String> params  = new HashMap<>();
        params.put("grant_type", "client_credentials");
        DoOnBackground action       = new RequestAuthTwitterAPI(handler, url, params);
        DoAsyncTasks doAsyncTasks   = new DoAsyncTasks(action);
        doAsyncTasks.execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public class LocalBinder extends Binder {
        public ServiceAuthTwitter getInstance() {
            return ServiceAuthTwitter.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler != null)
            handler = null;
    }
}
