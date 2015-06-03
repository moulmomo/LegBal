package com.example.mohamed.legapp2;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Mohamed on 18/05/2015.
 */
public class Beacon implements Parcelable{

    //public BluetoothDevice device;
    public String major;
    public String minor;
    //public Localisation localisation;
    public int rssi;

    public Beacon(){
     //this.device = device ;
    }

    public Beacon(Parcel in) {
        this.major = in.readString();
        this.minor = in.readString();
        this.rssi = in.readInt();
       // this.device = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(major);
        dest.writeString(minor);
        dest.writeInt(rssi);
        //dest.writeString(device);
    }

    public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {

        @Override
        public Beacon[] newArray(int size) {
            return new Beacon[size];
        }

        @Override
        public Beacon createFromParcel(Parcel source) {
            return new Beacon(source);
        }
    };

}
