package net.fahli.wsc_session1.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {
    private int id;
    private String type;
    private Bitmap bitmap;

    public Picture() {
    }

    protected Picture(Parcel in) {
        id = in.readInt();
        type = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeParcelable(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Picture> CREATOR = new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
