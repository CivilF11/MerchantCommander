package com.example.zacharydeboer.merchantcommander.Model;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.zacharydeboer.merchantcommander.Model.Enums.Faction;
import com.example.zacharydeboer.merchantcommander.Model.Enums.Status;

import java.util.Map;







public class Planet implements Parcelable
{
    //the wealth of the planet, from a range of 1 - 100
    private int mWealth;

    //the name of the planet. immutable
    private String mName;

    //the empire that this planet belongs to
    private Faction mFaction;

    //war time, peace, civil war
    private Status mStatus;

    //distance to center of galaxy. immutable
    private int mDistance;

    //angle from center of galaxy. immutable
    private int mAngle;




    //the "money" that the planet has
    private double mMoney;


    //the items that the planet has for trade
    private Map mInventory;


    //the array of items that this planet imports
    private Item[] mImports;


    //the array items that this planet produces and exports
    private Item[] mExports;


    //the array of natural resources that the planet contains
    private Item[] mResources;


    //the value of the planet from the previous update
    private int mPreviousValue;


    protected Planet(Parcel in)
    {
        mWealth = in.readInt();
        mName = in.readString();
        mDistance = in.readInt();
        mAngle = in.readInt();
        mMoney = in.readDouble();
        mImports = in.createTypedArray(Item.CREATOR);
        mExports = in.createTypedArray(Item.CREATOR);
        mResources = in.createTypedArray(Item.CREATOR);
        mPreviousValue = in.readInt();
    }

    public static final Creator<Planet> CREATOR = new Creator<Planet>()
    {
        @Override
        public Planet createFromParcel(Parcel in)
        {
            return new Planet(in);
        }

        @Override
        public Planet[] newArray(int size)
        {
            return new Planet[size];
        }
    };

    public int getWealth()
    {
        return mWealth;
    }

    public void setWealth(int value)
    {
        if (value > 100)
        {
            mWealth = 100;
        }
        else if (value < 1)
        {
            mWealth = 1;
        }
        else
        {
            mWealth = value;
        }
    }




    public String getName()
    {
        return mName;
    }




    public Faction getFaction()
    {
        return mFaction;
    }

    public void setFaction(Faction value)
    {
        mFaction = value;
    }


    public Status getStatus()
    {
        return mStatus;
    }

    public void setStatus(Status value)
    {
        mStatus = value;
    }



    public int getDistance()
    {
        return mDistance;
    }

    public int getAngle()
    {
        return mAngle;
    }





    //deprecated
    public Planet(String name, int distance, int angle, int wealth)
    {
        mName = name;
        mDistance = distance;
        mAngle = angle;
        mWealth = wealth;

        mPreviousValue = getValue();



    }


    public Planet(String name, int distance, int angle, int wealth, int previousValue, Item[] imports, Item[] exports, Item[] resources, Map<Integer, ItemStock> inventory)
    {
        mName = name;
        mDistance = distance;
        mAngle = angle;
        mWealth = wealth;

        mPreviousValue = previousValue;

        mImports = imports;
        mExports = exports;
        mResources = resources;

        mInventory = inventory;
    }







    //the daily update to the planet
    public void update()
    {
        sellExports();
        buyImports();
        harvestResources();

        if (getValue() > mPreviousValue)
        {
            increaseWealth();
        }
        else if (getValue() < mPreviousValue)
        {
            decreaseWealth();
        }
    }



    private int getValue()
    {
        int value = (int) mMoney;

        for (Object itemStock : mInventory.values())
        {
            value += (((ItemStock) itemStock).getItem().getBaseValue() * ((ItemStock) itemStock).getQuantity());
        }

        return value;
    }



    private void decreaseWealth()
    {
        mWealth--;
    }

    private void increaseWealth()
    {
        mWealth++;
    }



    private void sellExports()
    {
        //for every export
        for (int x = 0; x < mExports.length; x++)
        {
            //get the exported items stock from the inventory
            ItemStock itemStock = (ItemStock) mInventory.get(mExports[x].getId());

            //if the item exists in stock
            if (itemStock != null)
            {
                //if there is excess export
                if (itemStock.getQuantity() > mWealth)
                {
                    //sell wealth amount
                    mMoney += itemStock.getItem().getBaseValue() * mWealth;

                    itemStock.setQuantity(itemStock.getQuantity() - mWealth);
                }
                //else there is less than wealth amount of export
                else
                {
                    //sell all of it
                    mMoney += itemStock.getItem().getBaseValue() * itemStock.getQuantity();

                    itemStock.setQuantity(0);
                }

                //update the inventory
                mInventory.put(mExports[x].getId(), itemStock);
            }

        }
    }


    private void buyImports()
    {
        //for every import
        for (int x = 0; x < mImports.length; x++)
        {
            //get the imported items stock from the inventory
            ItemStock itemStock = (ItemStock) mInventory.get(mImports[x].getId());

            //if more of the imported item needs to be bought
            if (itemStock.getQuantity() < mWealth)
            {
                //the quantity of the item that needs to be bought
                int toBuy = mWealth - itemStock.getQuantity();

                //if can afford that much of the item
                if (mMoney > itemStock.getItem().getBaseValue() * toBuy)
                {
                    //buy it
                    mMoney -= itemStock.getItem().getBaseValue() * toBuy;

                    //add it to the inventory
                    itemStock.setQuantity(mWealth);
                }
                //else
                else
                {
                    //buy as much as possible
                    while (mMoney < itemStock.getItem().getBaseValue() * toBuy)
                    {
                        toBuy--;
                    }

                    mMoney -= itemStock.getItem().getBaseValue() * toBuy;

                    itemStock.setQuantity(mWealth);
                }
            }

            //update the inventory
            mInventory.put(mImports[x].getId(), itemStock);
        }
    }



    private void harvestResources()
    {
        int toHarvest = (mWealth / 10) + 1;

        //for every natural resource on the planet
        for (Item item : mResources)
        {
            //get the stock of the item from the inventory
            ItemStock itemStock = (ItemStock) mInventory.get(item.getId());

            //add 10% of wealth to resource stock
            itemStock.setQuantity(itemStock.getQuantity() + toHarvest);

            //for every export
            for (Item export : mExports)
            {
                //if the natural resource is also an export
                if (item.getId() == export.getId())
                {
                    //add 90% of wealth, making a total 100% of wealth added
                    itemStock.setQuantity(itemStock.getQuantity() + (toHarvest * 9));

                    break;
                }
            }

            mInventory.put(item.getId(), itemStock);

        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mWealth);
        dest.writeString(mName);
        dest.writeInt(mDistance);
        dest.writeInt(mAngle);
        dest.writeDouble(mMoney);
        dest.writeTypedArray(mImports, flags);
        dest.writeTypedArray(mExports, flags);
        dest.writeTypedArray(mResources, flags);
        dest.writeInt(mPreviousValue);
    }
}
