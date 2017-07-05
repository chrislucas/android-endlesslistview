package imagineapps.testrequestafterscroll.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import imagineapps.testrequestafterscroll.R;
import imagineapps.testrequestafterscroll.utils.UtilsSimpleFormatDate;
import imagineapps.testrequestafterscroll.entitiy.Info;

/**
 * Created by r028367 on 03/07/2017.
 */

public class AdapterListView extends ArrayAdapter<Info> {

    private Context context;
    private List<Info> list;

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     * @param list            The objects to represent in the ListView.
     */
    public AdapterListView(@NonNull Context context, @LayoutRes int resource
            , @NonNull List<Info> list) {
        super(context, resource, list);
        this.context    = context;
        this.list       = list;
    }

    private class ViewHolder {
        private ImageView imageInfo;
        private TextView title, textInfo, date;
        public ViewHolder() {}

        public ImageView getImageInfo() {
            return imageInfo;
        }

        public void setImageInfo(ImageView imageInfo) {
            this.imageInfo = imageInfo;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getTextInfo() {
            return textInfo;
        }

        public void setTextInfo(TextView textInfo) {
            this.textInfo = textInfo;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }
    }

    @Nullable
    @Override
    public Info getItem(int position) {
        return position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        Info info = list.get(position);
        if(info != null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                convertView         = layoutInflater.inflate(R.layout.adapter_layout_info, null);
                viewHolder          = new ViewHolder();
                ImageView imageView = (ImageView) convertView.findViewById(R.id.image_info);
                TextView textInfo   = (TextView) convertView.findViewById(R.id.text_info);
                TextView titleInfo  = (TextView) convertView.findViewById(R.id.title_info);
                TextView dateInfo   = (TextView) convertView.findViewById(R.id.date_info);
                viewHolder.setImageInfo(imageView);
                viewHolder.setTextInfo(textInfo);
                viewHolder.setTitle(titleInfo);
                viewHolder.setDate(dateInfo);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Bitmap bitmap = info.getImage();
            if(bitmap != null) {
                viewHolder.getImageInfo().setImageBitmap(bitmap);
            }
            viewHolder.getTextInfo().setText(info.getText());
            viewHolder.getTitle().setText(info.getTitle());
            viewHolder.getDate().setText(UtilsSimpleFormatDate.convertLongToDateFormat(info.getDate(), "EEE MMM d, yyyy HH:mm:ss"));
        }
        return convertView;
    }
}
