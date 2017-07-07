package imagineapps.testrequestafterscroll.services.connection.handler;

import android.os.Handler;
import android.os.Message;
import android.telecom.Call;


/**
 * Created by r028367 on 07/07/2017.
 */

public class HandlerDownloadBitmap extends Handler {

    private CallbackHandlerMessage callbackHandlerMessage;
    public HandlerDownloadBitmap(CallbackHandlerMessage callbackHandlerMessage) {
        this.callbackHandlerMessage = callbackHandlerMessage;
    }

    /**
     * Subclasses must implement this to receive messages.
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }
}
