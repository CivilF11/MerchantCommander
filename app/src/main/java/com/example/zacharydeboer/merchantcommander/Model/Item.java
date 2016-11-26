package com.example.zacharydeboer.merchantcommander.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable
{
    //the id of the item
    private int mId;

    //the name of the item
    private String mName;

    //base value of the item
    private double mBaseValue;


    protected Item(Parcel in)
    {
        mId = in.readInt();
        mName = in.readString();
        mBaseValue = in.readDouble();
    }



    public static final Creator<Item> CREATOR = new Creator<Item>()
    {
        @Override
        public Item createFromParcel(Parcel in)
        {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size)
        {
            return new Item[size];
        }
    };

    public int getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public double getBaseValue()
    {
        return mBaseValue;
    }



    public Item(int id, String name, double baseValue)
    {
        mId = id;

        mName = name;

        mBaseValue = baseValue;
    }



    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeDouble(mBaseValue);
    }
}
