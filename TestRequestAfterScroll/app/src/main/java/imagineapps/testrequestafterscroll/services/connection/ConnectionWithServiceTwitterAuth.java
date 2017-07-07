package imagineapps.testrequestafterscroll.services.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import imagineapps.testrequestafterscroll.services.ServiceAuthTwitter;

/**
 * Created by r028367 on 07/07/2017.
 */

public class ConnectionWithServiceTwitterAuth implements ServiceConnection {

    private ServiceAuthTwitter serviceAuthTwitter;
    private Handler handler;

    public ConnectionWithServiceTwitterAuth(Handler handler) {
        this.handler = handler;
    }

    public ServiceAuthTwitter getServiceAuthTwitter() {
        return serviceAuthTwitter;
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
        serviceAuthTwitter  = ((ServiceAuthTwitter.LocalBinder) service).getInstance();
        serviceAuthTwitter.setHandler(handler);
        serviceAuthTwitter.doRequest();
        /*
        Messenger messenger = serviceAuthTwitter.getMessanger();
        Message message     = new Message();
        message.what        = ServiceAuthTwitter.MESSAGE_SERVICE;
        try {
            messenger.send(message);
        }
        catch (RemoteException e) {
            Log.e("ON_SERVICE_CONNECTED", e.getMessage());
        }
        */
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
        serviceAuthTwitter = null;
        Log.v("SERVICE_DISCONNECTION", "TWITTER_AUTH");
    }
}
