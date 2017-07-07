package imagineapps.testrequestafterscroll.services.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import imagineapps.testrequestafterscroll.services.ServiceDownloadBitmap;

/**
 * Created by r028367 on 07/07/2017.
 */

public class ConnectionWithDownloadBitmap implements ServiceConnection {

    private Handler handler;
    private ServiceDownloadBitmap serviceDownloadBitmap;

    public ConnectionWithDownloadBitmap(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * Called when a connection to the Service has been established, with
     * the {@link IBinder} of the communication channel to the
     * Service.
     *
     * @param name    The concrete component name of the service that has
     *                been connected.
     * @param service The IBinder of the Service's communication channel,
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceDownloadBitmap = ( (ServiceDownloadBitmap.LocalBinder) service).getInstance();
        serviceDownloadBitmap.setHandler(getHandler());
        serviceDownloadBitmap.doRequest();
    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceDownloadBitmap = null;
        Log.v("SERVICE_DISCONNECTION", "DOWNLOAD_BITMAP");
    }
}
