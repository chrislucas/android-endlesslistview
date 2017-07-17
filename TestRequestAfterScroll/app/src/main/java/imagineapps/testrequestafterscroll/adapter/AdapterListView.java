package imagineapps.testrequestafterscroll.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import imagineapps.testrequestafterscroll.R;
import imagineapps.testrequestafterscroll.utils.UtilsSimpleFormatDate;
import imagineapps.testrequestafterscroll.entitiies.Info;

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
        private TextView title, subtitle, textInfo, date, timeAgo;
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

        public TextView getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(TextView subtitle) {
            this.subtitle = subtitle;
        }

        public TextView getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(TextView timeAgo) {
            this.timeAgo = timeAgo;
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
                TextView subTitleInfo  = (TextView) convertView.findViewById(R.id.subtitle_info);
                TextView dateInfo   = (TextView) convertView.findViewById(R.id.date_info);
                TextView timeAgo    = (TextView) convertView.findViewById(R.id.time_ago);
                viewHolder.setImageInfo(imageView);
                viewHolder.setTextInfo(textInfo);
                viewHolder.setTitle(titleInfo);
                viewHolder.setSubtitle(subTitleInfo);
                viewHolder.setDate(dateInfo);
                viewHolder.setTimeAgo(timeAgo);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Bitmap bitmap = info.getImage();
            if(bitmap != null) {
                viewHolder.getImageInfo().setImageBitmap(bitmap);
            }
            else {
                final String url = info.getUrlImage();
                final Picasso picasso = Picasso.with(context);
                /**
                 * Indicador de cache
                 * RED: A venho da rede
                 * GREEN: Memoria Volatil
                 * BLUE: Disco
                 * */
                picasso.setIndicatorsEnabled(true);
                /**
                 * Forma que a biblioteca faz cache das imagens
                 * https://futurestud.io/tutorials/picasso-influencing-image-caching
                 *
                 * A biblioteca verifica se a imagem esta na memoria cache virtual,
                 * depois na memoria cache em disco, caso nao esteja em cache, a image
                 * e baixada.
                 * https://square.github.io/picasso/2.x/picasso/com/squareup/picasso/NetworkPolicy.html#OFFLINE
                 * */
                final ViewHolder finalInstance = viewHolder;
                picasso.load(url)
                        // ignora a pesquisa pela imagem no cache de memoria volatil
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        // nao armazena a imagem em cache apos o downlaod
                        //.memoryPolicy(MemoryPolicy.NO_STORE)
                        // nao pesquisa imagem na memoria volatil, forçando a pesquisa usando a internet
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        // nao armazena imagem na memoria permanente
                        //.networkPolicy(NetworkPolicy.NO_STORE)
                        // busca a imagem no cache em disco, nao busca na rede
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.placeholder1)
                        .error(R.drawable.erro_placeholder1)
                        .into(viewHolder.getImageInfo(), new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.i("GET_IMAGE_OFFLINE", "SUCCESS");
                            }
                            @Override
                            public void onError() {
                                Log.e("GET_IMAGE_OFFLINE", "FAILURED");
                                picasso.load(url)
                                        .placeholder(R.drawable.placeholder1)
                                        .error(R.drawable.erro_placeholder1)
                                        .into(finalInstance.getImageInfo(), new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.i("GET_IMAGE_ONLINE", "SUCCESS");
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("GET_IMAGE_ONLINE", "FAILURED");
                                            }
                                        });
                            }
                        });

            }
            viewHolder.getTextInfo().setText(info.getText());
            viewHolder.getTitle().setText(info.getTitle());
            viewHolder.getTitle().setPaintFlags(viewHolder.getTitle().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            viewHolder.getSubtitle().setText(info.getSubtitle());
            // "EEE MMM d, yyyy HH:mm:ss"
            viewHolder.getDate().setText(UtilsSimpleFormatDate.convertLongToDateFormat(info.getDate(), "d, MMM yyyy HH:mm"));
            viewHolder.getTimeAgo().setText(info.getTimeAgo());
        }
        return convertView;
    }
}
