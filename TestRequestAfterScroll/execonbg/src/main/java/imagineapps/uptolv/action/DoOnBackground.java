package imagineapps.uptolv.action;

import android.os.Parcelable;

import java.util.List;

/**
 * Created by r028367 on 03/07/2017.
 */

public interface DoOnBackground {
    public List<? extends Parcelable> execute();
    public void afterExecution(List<Parcelable> data);
}
