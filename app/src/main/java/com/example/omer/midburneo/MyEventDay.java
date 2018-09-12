package com.example.omer.midburneo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;

class MyEventDay extends EventDay implements Parcelable {
    private String mNote;

    MyEventDay(Calendar day, int imageResource, String note) {
        super(day, imageResource);
        mNote = note;
        Log.i("ssssssssssss", "MyEventDay"+"MyEventDay");

    }
    String getNote() {
        return mNote;
    }
    private MyEventDay(Parcel in) {
        super((Calendar) in.readSerializable(), in.readInt());
        mNote = in.readString();

        Log.i("ssssssssssss", "MyEventDay"+"getNote");

    }
    public static final Creator<MyEventDay> CREATOR = new Creator<MyEventDay>() {
        @Override
        public MyEventDay createFromParcel(Parcel in) {
            Log.i("ssssssssssss", "MyEventDay"+"createFromParcel");

            return new MyEventDay(in);
        }
        @Override
        public MyEventDay[] newArray(int size) {
            Log.i("f", "MyEventDay"+size);

            return new MyEventDay[size];
        }

    };
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(getCalendar());
        parcel.writeInt(getImageResource());
        parcel.writeString(mNote);
        Log.i("ssssssssssss", "MyEventDay"+"writeToParcel");

    }
    @Override
    public int describeContents() {
        Log.i("ssssssssssss", "MyEventDay"+"describeContents");

        return 0;

    }
}