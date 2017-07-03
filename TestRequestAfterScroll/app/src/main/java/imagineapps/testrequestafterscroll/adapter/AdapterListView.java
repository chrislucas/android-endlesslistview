package imagineapps.testrequestafterscroll.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import imagineapps.testrequestafterscroll.entitiy.Info;

/**
 * Created by r028367 on 03/07/2017.
 */

public class AdapterListView extends ArrayAdapter<Info> {

    private Context context;

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     * @param objects            The objects to represent in the ListView.
     */
    public AdapterListView(@NonNull Context context, @LayoutRes int resource
            , @NonNull List<Info> objects) {
        super(context, resource, objects);
        this.context = context;
    }


    public static class ViewHolder {

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
