package com.example.zacharydeboer.merchantcommander.Model;

public class ItemStock
{
    private Item mItem;

    private int mQuantity;




    public Item getItem()
    {
        return mItem;
    }



    public int getQuantity()
    {
        return mQuantity;
    }

    public void setQuantity(int value)
    {
        mQuantity = value;
    }



    public ItemStock(Item item)
    {
        mItem = item;

        mQuantity = 0;
    }



    public ItemStock(Item item, int quantity)
    {
        mItem = item;

        mQuantity = quantity;
    }
}
