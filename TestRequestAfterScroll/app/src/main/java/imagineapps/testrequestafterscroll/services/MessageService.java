package imagineapps.testrequestafterscroll.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class MessageService extends Service {
    public MessageService() {
    }

    /**
     * Criando uma especializacao de handler que podera se comunicar com
     * a classe que chamar esse serviço
     * */
    public class CustomHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger messenger = new Messenger(new CustomHandler());

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  messenger.getBinder();
    }
}
