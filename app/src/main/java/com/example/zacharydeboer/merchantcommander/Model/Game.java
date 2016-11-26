package com.example.zacharydeboer.merchantcommander.Model;


import java.util.ArrayList;
import java.util.List;

public class Game
{

    private List<Planet> mPlanetList;

    private ArrayList<Item> mItemList;




    public ArrayList<Item> getItemList()
    {
        return mItemList;
    }



    public Game(List<Planet> planetList, ArrayList<Item> itemList)
    {
        mPlanetList = planetList;

        mItemList = itemList;
    }



    public Game()
    {
        mItemList = new ArrayList<>();

        mPlanetList = new ArrayList<>();
    }



    public void addPlanet(Planet planet)
    {
        mPlanetList.add(planet);
    }



    public void addItem(Item item)
    {
        mItemList.add(item);
    }

}
