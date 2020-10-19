package net.fahli.wsc_session1.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class History implements Parcelable {
    private String relocationDate, OldDepartment, NewDepartment, OldAssetSN, NewAssetSN;

    public History() {
    }

    protected History(Parcel in) {
        relocationDate = in.readString();
        OldDepartment = in.readString();
        NewDepartment = in.readString();
        OldAssetSN = in.readString();
        NewAssetSN = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(relocationDate);
        dest.writeString(OldDepartment);
        dest.writeString(NewDepartment);
        dest.writeString(OldAssetSN);
        dest.writeString(NewAssetSN);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public String getRelocationDate() {
        return relocationDate;
    }

    public void setRelocationDate(String relocationDate) {
        this.relocationDate = relocationDate;
    }

    public String getOldDepartment() {
        return OldDepartment;
    }

    public void setOldDepartment(String oldDepartment) {
        OldDepartment = oldDepartment;
    }

    public String getNewDepartment() {
        return NewDepartment;
    }

    public void setNewDepartment(String newDepartment) {
        NewDepartment = newDepartment;
    }

    public String getOldAssetSN() {
        return OldAssetSN;
    }

    public void setOldAssetSN(String oldAssetSN) {
        OldAssetSN = oldAssetSN;
    }

    public String getNewAssetSN() {
        return NewAssetSN;
    }

    public void setNewAssetSN(String newAssetSN) {
        NewAssetSN = newAssetSN;
    }
}
