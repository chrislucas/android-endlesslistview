package imagineapps.uptolv.action;

import android.os.AsyncTask;
import android.os.Parcelable;

/**
 * Created by r028367 on 03/07/2017.
 */

public class ExecuteAsyncTask<Params, Counter, ListData extends Object>
        extends AsyncTask <Params, Counter, ListData> {
    public ExecuteAsyncTask() {
    }

    @Override
    protected ListData doInBackground(Params... params) {
        return null;
    }
}
