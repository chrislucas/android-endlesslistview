package imagineapps.testrequestafterscroll.entitiy;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by r028367 on 03/07/2017.
 */

public class Info implements Parcelable {

    private Bitmap image;
    private String title, text;


    public Info() {
    }

    public Info(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}


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
