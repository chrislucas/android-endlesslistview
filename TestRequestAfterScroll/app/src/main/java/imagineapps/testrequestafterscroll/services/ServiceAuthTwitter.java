package imagineapps.testrequestafterscroll.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import imagineapps.testrequestafterscroll.http.RequestAuthTwitterAPI;
import imagineapps.uptolv.action.DoOnBackground;
import imagineapps.uptolv.action.DoAsyncTasks;

public class ServiceAuthTwitter extends Service {

    private Handler handler;
    private final IBinder iBinder = new LocalBinder();
    //private final Messenger messanger = new Messenger(new LocalMessage(this));

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
        Log.i("SERVICE_TWITTER_AUTH", "ONCREATE");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("SERVICE_TWITTER_AUTH", "ONBIND");
        return iBinder;
    }

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

    public  class LocalBinder extends Binder  {
        public ServiceAuthTwitter getInstance() {
            return ServiceAuthTwitter.this;
        }
    }

    public static final int MESSAGE_SERVICE = 0;

    public  class LocalMessage extends Handler {
        private final WeakReference<ServiceAuthTwitter> mReference;

        public LocalMessage(ServiceAuthTwitter mReference) {
            this.mReference = new WeakReference<ServiceAuthTwitter>(mReference);
        }

        @Override
        public void handleMessage(Message msg) {
            ServiceAuthTwitter service = mReference.get();
            if(msg.what == MESSAGE_SERVICE) {}
            super.handleMessage(msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
