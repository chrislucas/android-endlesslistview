package imagineapps.uptolv.action;

import android.os.Parcelable;


/**
 * Created by r028367 on 03/07/2017.
 */

public interface ExecuteOnBackground {
    public Object execute();
    public void afterExecution(Object data);
}
