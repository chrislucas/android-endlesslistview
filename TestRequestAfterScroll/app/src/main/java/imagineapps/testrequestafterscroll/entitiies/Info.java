package imagineapps.testrequestafterscroll.entitiies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;

import imagineapps.testrequestafterscroll.utils.UtilsSimpleFormatDate;

/**
 * Created by r028367 on 03/07/2017.
 */

public class Info implements Parcelable, Comparable {

    private String id;
    private Bitmap image;
    private byte [] imageBuffer;
    private String title, subtitle, text, urlImage;
    private Long date;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImageBuffer() {
        return imageBuffer;
    }

    public void setImageBuffer(byte[] imageBuffer) {
        this.imageBuffer = imageBuffer;
    }

    public Info() {}

    public Info(Parcel in) {
        this.title       = in.readString();
        this.text        = in.readString();
        this.subtitle    = in.readString();
        this.urlImage    = in.readString();
        this.image       = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        this.date        = (Long) in.readValue(Long.class.getClassLoader());
        this.id          = (String) in.readValue(String.class.getClassLoader());
        this.imageBuffer = (byte[]) in.readValue(Byte.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.subtitle);
        dest.writeString(this.urlImage);
        dest.writeValue(this.image);
        dest.writeValue(this.date);
        dest.writeValue(this.id);
        dest.writeValue(this.imageBuffer);
    }

    public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel source) {
            return new Info(source);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    // UtilsSimpleFormatDate.DEFAULT_UTC_FORMAT_DATE
    public String getDateFormat(String format) {
        String date = UtilsSimpleFormatDate.convertLongToDateFormat(this.date, format);
        return date;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Info info = (Info) o;
        long c = info.getDate() - this.getDate();
        return ( c < 0 ) ? -1 : (c == 0) ? 0 : 1;
    }

    /**
     * TODO
     *
     * */
    public String getTimeAgo() {
        long createAt       = this.date;
        Calendar calendar       = Calendar.getInstance();
        // aprox
        // 1 ano  = 31104000000 ms
        // 1 mes  = 2592000000 ms
        // 1 dia  = 86400000 ms
        // 1 hora = 3600000 ms
        long currentTime = calendar.getTimeInMillis();
        long diffInSeconds = (currentTime - createAt) / 1000;
        String message = "";
        if(diffInSeconds < 60) {
            message = String.format("Há aprox. %d seg.", diffInSeconds);
        }
        else if(diffInSeconds > 59 && diffInSeconds < 3600) {
            diffInSeconds /= 60;
            message = String.format("Há aprox. %d min.", diffInSeconds);
        }

        else if(diffInSeconds > 3599 &&  diffInSeconds < 86400) {
            diffInSeconds /= 3600;
            message = String.format("Há aprox. %d %s.", diffInSeconds, diffInSeconds > 1 ? "horas" : "hora");
        }

        else {
            if((diffInSeconds / 86400) < 31) {
                // numero de segundos num dia
                diffInSeconds /= 86400;
                message = String.format("Há aprox. %d %s.", diffInSeconds, diffInSeconds > 1 ? "dias" : "dias");
            }
            else {
                // numero de segundos em um mês de 30 dias = 2592000
                if( (diffInSeconds / 2592000) < 12) {
                    diffInSeconds /= 2592000;
                    message = String.format("Há aprox. %d %s.", diffInSeconds, diffInSeconds > 1 ? "meses" : "mês");
                }
                else {
                    diffInSeconds /= 31104000;
                    message = String.format("Há aprox. %d %s.", diffInSeconds, diffInSeconds > 1 ? "anos" : "ano");
                }
            }
        }
        return message;
    }
}
