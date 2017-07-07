package imagineapps.testrequestafterscroll.services.old;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import imagineapps.testrequestafterscroll.services.ServiceAuthTwitter;
import imagineapps.testrequestafterscroll.services.ServiceSearchTwitterAPI;

/**
 * Created by r028367 on 03/07/2017.
 */

public class StartServiceImpl {

    private Context context;
    private IntentService intentService;


    public StartServiceImpl(Context context) {
        this.context = context;
    }

    public StartServiceImpl(Context context, IntentService intentService) {
        this.context = context;
        this.intentService = intentService;
    }

    public void startService() {
        Intent intent = new Intent(context, intentService.getClass());
        context.startService(intent);
    }

    public void getAuthorization() {
        Intent intent = new Intent(context, ServiceAuthTwitter.class);
        context.startService(intent);
    }

    public void start() {
        Intent intent = new Intent(context, ServiceSearchTwitterAPI.class);
        context.startService(intent);
    }



}
