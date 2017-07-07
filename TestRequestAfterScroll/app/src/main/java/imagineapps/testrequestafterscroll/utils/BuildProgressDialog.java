package imagineapps.testrequestafterscroll.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

/**
 * Created by r028367 on 07/07/2017.
 */

public class BuildProgressDialog {

    private Context context;
    private ProgressDialog p;
    private Handler handler;

    public BuildProgressDialog(Context context) {
        this.context = context;
        p = new ProgressDialog(context);
        handler = new Handler(Looper.getMainLooper());
    }

    public BuildProgressDialog (Context context, Handler.Callback callback) {
        p       = new ProgressDialog(context);
        handler = new Handler(callback);
    }


    public BuildProgressDialog buildDefault(boolean isIndeterminate
            , boolean isCancelable, String title, String message) {
        p.setIndeterminate(isIndeterminate);
        p.setCancelable(isCancelable);
        p.setTitle(title);
        p.setMessage(message);
        return this;
    }

    public boolean isShowing() {
        return p.isShowing();
    }

    public ProgressDialog get() {
        return p;
    }

    public ProgressDialog setMessage(String message) {
        p.setMessage(message);
        return p;
    }

    public ProgressDialog setTitle(String title) {
        p.setTitle(title);
        return p;
    }

    public ProgressDialog setStyle(int style) {
        p.setProgressStyle(style);
        return p;
    }

    public ProgressDialog setCancelMessage(Message message) {
        p.setCancelMessage(message);
        return p;
    }

    public ProgressDialog setCancelableMessage(boolean canceledOnTouch) {
        p.setCanceledOnTouchOutside(canceledOnTouch);
        return p;
    }

    public ProgressDialog setDismissMessage(Message message) {
        p.setDismissMessage(message);
        return p;
    }

    public void safeShowing() throws Exception {
        if(context instanceof Activity) {
            final Activity activity = (Activity) context;
            if(!activity.isFinishing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!p.isShowing()) {
                            p.show();
                        }
                    }
                });
            }
        }
        else {
            throw new Exception("Context must be child of Activity");
        }
    }

    public void safeDismiss() throws Exception {
        if(context instanceof Activity) {
            final Activity activity = (Activity) context;
            if(!activity.isFinishing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(p.isShowing()) {
                            p.dismiss();
                        }
                    }
                });
            }
        }
        else {
            throw new Exception("Context must be child of Activity");
        }
    }

    /**
     * Testando algumas ideias malucos.
     *
     * */
    interface Task<T> {
        public T get();
        public List<T> getList();
    }

    public <Result> Result getData(Task<Result> task) throws Exception {
        safeShowing();
        Result  result = (Result) task.get();
        safeDismiss();
        return result;
    }

    public <Result> List<Result> getListData(Task<Result> task) throws Exception {
        safeShowing();
        List<Result>  results = (List<Result>) task.getList();
        safeDismiss();
        return results;
    }


}
