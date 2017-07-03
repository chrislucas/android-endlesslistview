package imagineapps.testrequestafterscroll.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import imagineapps.testrequestafterscroll.http.SearchTwitterAPI;
import imagineapps.uptolv.action.DoAsyncTasks;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServiceSearchTwitterAPI extends IntentService {

    public ServiceSearchTwitterAPI() {
        super("ServiceSearchTwitterAPI");
    }

    private String querySearch, authorization;

    public static final String STR_SEARCH   = "STR_SEARCH";
    public static final String STR_AUTH     = "STR_AUTH";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle   = intent.getExtras();
            querySearch     = bundle.getString(STR_SEARCH);
            authorization   = bundle.getString(STR_AUTH);
            doRequest();
        }
    }

    private void doRequest() {
        String url = "https://api.twitter.com/1.1/search/tweets.json?q=";
        url = Uri.parse(url + querySearch).toString();
        SearchTwitterAPI httpRequest = new SearchTwitterAPI(url, authorization);
        DoAsyncTasks doAsyncTasks    = new DoAsyncTasks(httpRequest);
        doAsyncTasks.execute();
    }
}
