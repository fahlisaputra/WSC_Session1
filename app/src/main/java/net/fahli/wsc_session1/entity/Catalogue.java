package net.fahli.wsc_session1.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Catalogue implements Parcelable {
    private int ID;
    private String AssetSN, AssetName, WarrantyDate, DepartmentName, AssetGroupName, Description;
    private int EmployeeID, AssetGroupID, DepartmentLocationID, DepartmentID;
    private ArrayList<String> photos;

    public Catalogue() {
    }

    protected Catalogue(Parcel in) {
        ID = in.readInt();
        AssetSN = in.readString();
        AssetName = in.readString();
        WarrantyDate = in.readString();
        DepartmentName = in.readString();
        AssetGroupName = in.readString();
        Description = in.readString();
        EmployeeID = in.readInt();
        AssetGroupID = in.readInt();
        DepartmentLocationID = in.readInt();
        DepartmentID = in.readInt();
        photos = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(AssetSN);
        dest.writeString(AssetName);
        dest.writeString(WarrantyDate);
        dest.writeString(DepartmentName);
        dest.writeString(AssetGroupName);
        dest.writeString(Description);
        dest.writeInt(EmployeeID);
        dest.writeInt(AssetGroupID);
        dest.writeInt(DepartmentLocationID);
        dest.writeInt(DepartmentID);
        dest.writeStringList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Catalogue> CREATOR = new Creator<Catalogue>() {
        @Override
        public Catalogue createFromParcel(Parcel in) {
            return new Catalogue(in);
        }

        @Override
        public Catalogue[] newArray(int size) {
            return new Catalogue[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAssetSN() {
        return AssetSN;
    }

    public void setAssetSN(String assetSN) {
        AssetSN = assetSN;
    }

    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        AssetName = assetName;
    }

    public String getWarrantyDate() {
        return WarrantyDate;
    }

    public void setWarrantyDate(String warrantyDate) {
        WarrantyDate = warrantyDate;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public String getAssetGroupName() {
        return AssetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        AssetGroupName = assetGroupName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public int getAssetGroupID() {
        return AssetGroupID;
    }

    public void setAssetGroupID(int assetGroupID) {
        AssetGroupID = assetGroupID;
    }

    public int getDepartmentLocationID() {
        return DepartmentLocationID;
    }

    public void setDepartmentLocationID(int departmentLocationID) {
        DepartmentLocationID = departmentLocationID;
    }

    public int getDepartmentID() {
        return DepartmentID;
    }

    public void setDepartmentID(int departmentID) {
        DepartmentID = departmentID;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }
}
