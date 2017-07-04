package imagineapps.testrequestafterscroll.entitiy;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by r028367 on 03/07/2017.
 */

public class Info implements Parcelable {

    private String id;
    private Bitmap image;
    private String title, text, urlImage;
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

    public Info() {}

    public Info(Parcel in) {
        this.title      = in.readString();
        this.text       = in.readString();
        this.urlImage   = in.readString();
        this.image      = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        this.date       = (Long) in.readValue(Long.class.getClassLoader());
        this.id         = (String) in.readValue(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.urlImage);
        dest.writeValue(this.image);
        dest.writeValue(this.date);
        dest.writeValue(this.id);
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



}
