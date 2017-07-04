package imagineapps.testrequestafterscroll.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import imagineapps.testrequestafterscroll.http.SearchTwitterAPI;
import imagineapps.uptolv.action.DoAsyncTasks;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServiceSearchTwitterAPI extends Service /*IntentService*/ {

    private String querySearch, authorization, url;
    public static final String TEXT_SEARCH          = "TEXT_SEARCH";
    public static final String TOKEN_AUTHORIZATION  = "TOKEN_AUTHORIZATION";
    public static final String URL                  = "URL";

    private Handler handler;
    private final IBinder iBinder = new LocalBinder();

    public ServiceSearchTwitterAPI() {}

    public class LocalBinder extends Binder{
        public ServiceSearchTwitterAPI getInstance() {
            return ServiceSearchTwitterAPI.this;
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            Bundle bundle   = intent.getExtras();
            if(bundle != null) {
                querySearch     = bundle.getString(TEXT_SEARCH);
                authorization   = bundle.getString(TOKEN_AUTHORIZATION);
                url             = bundle.getString(URL);
            }
        }
        return iBinder;
    }

    /**
     * Executado uma unica vez, quando o serviço eh criado. Esse eh um bom lugar para
     * configurar alguma coisa caso necessario
     * */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    // @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    /**
     * Executado quando o serviço for iniciado atraves do metodo startSerice
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle bundle   = intent.getExtras();
            if(bundle != null) {
                querySearch     = bundle.getString(TEXT_SEARCH);
                authorization   = bundle.getString(TOKEN_AUTHORIZATION);
                url             = bundle.getString(URL);
            }
        }
        return START_NOT_STICKY;
    }

    public void doRequest() {
        SearchTwitterAPI httpRequest = new SearchTwitterAPI(url, authorization, handler);
        DoAsyncTasks doAsyncTasks    = new DoAsyncTasks(httpRequest);
        doAsyncTasks.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
