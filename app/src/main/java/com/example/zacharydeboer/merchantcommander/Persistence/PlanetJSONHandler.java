package com.example.zacharydeboer.merchantcommander.Persistence;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.zacharydeboer.merchantcommander.MainActivity;
import com.example.zacharydeboer.merchantcommander.Model.Item;
import com.example.zacharydeboer.merchantcommander.Model.ItemStock;
import com.example.zacharydeboer.merchantcommander.Model.Planet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PlanetJSONHandler extends IntentService implements IJSONHandler
{


    public final static String S_PLANET = "PLANET";




    private String mDirectory;

    private boolean mIsConnected;

    private Planet mPlanet;

    private int mNumberOfPlanets;

    private List<Item> mItemList;



    PlanetJSONHandler()
    {
        super("PlanetJSONHandler");
    }


    @Override
    public void loadFromJSON()
    {

        for (int i = 0; i < mNumberOfPlanets; i++)
        {
            if (mIsConnected)
            {

                try
                {
                    InputStream inputStream;

                    URL url = new URL(URL + mDirectory + i + ".json");

                    URLConnection connection = url.openConnection();

                    HttpURLConnection httpConnection = (HttpURLConnection) connection;



                    int responseCode = httpConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK)
                    {

                        inputStream = httpConnection.getInputStream();

                        Scanner scanner = new Scanner(inputStream);

                        StringBuilder stringBuilder = new StringBuilder();

                        while(scanner.hasNextLine())
                        {
                            stringBuilder.append(scanner.nextLine());
                        }

                        mPlanet = parseJSONFile(stringBuilder.toString());



                        Intent broadcastIntent = new Intent();

                        broadcastIntent.setAction(MainActivity.PlanetBroadcastReceiver.LOAD_RESPONSE);

                        broadcastIntent.putExtra(S_PLANET, mPlanet);

                        sendBroadcast(broadcastIntent);

                    }



                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
            else
            {
                Log.e("PlanetJSONHandler", "No Connection");
            }
        }




    }



    @Override
    public Planet parseJSONFile(String jsonData)
    {
        StringBuilder planetBuilder = new StringBuilder();

        try
        {
            JSONObject wrapper = new JSONObject(jsonData);



            String planetName = wrapper.getString("planetName");




            int wealth = Integer.parseInt(wrapper.getString("wealth"));




            int previousValue = Integer.parseInt(wrapper.getString("previousValue"));




            int distance = Integer.parseInt(wrapper.getString("distance"));




            int angle = Integer.parseInt(wrapper.getString("angle"));





            JSONArray importsArray = wrapper.getJSONArray("imports");

            Item[] imports = new Item[importsArray.length()];



            for (int x = 0; x < imports.length; x++)
            {

                int id = Integer.parseInt(importsArray.getString(x));

                for (int i = 0; i < mItemList.size(); i++)
                {
                    if (id == mItemList.get(i).getId())
                    {
                        imports[x] = mItemList.get(i);

                        break;
                    }
                }

            }



            JSONArray exportsArray = wrapper.getJSONArray("exports");

            Item[] exports = new Item[exportsArray.length()];


            for (int x = 0; x < exports.length; x++)
            {
                int id = Integer.parseInt(exportsArray.getString(x));

                for (int i = 0; i < mItemList.size(); i++)
                {
                    if (id == mItemList.get(i).getId())
                    {
                        exports[x] = mItemList.get(i);

                        break;
                    }
                }
            }



            JSONArray naturalResourcesArray = wrapper.getJSONArray("naturalResources");

            Item[] naturalResources = new Item[naturalResourcesArray.length()];

            for (int x = 0; x < naturalResources.length; x++)
            {
                int id = Integer.parseInt(naturalResourcesArray.getString(x));

                for (int i = 0; i < mItemList.size(); i++)
                {
                    naturalResources[x] = mItemList.get(i);

                    break;
                }
            }



            JSONArray inventoryArray = wrapper.getJSONArray("inventory");

            JSONObject[] inventory = new JSONObject[inventoryArray.length()];

            for (int x = 0; x < inventory.length; x++)
            {
                inventory[x] = inventoryArray.getJSONObject(x);
            }

            Map inventoryMap = new HashMap<Integer, ItemStock>();



            for (int x = 0; x < inventory.length; x++)
            {

                int id = Integer.parseInt(inventory[x].getString("itemId"));

                int count = Integer.parseInt(inventory[x].getString("count"));

                Item item = null;

                for (int i = 0; i < mItemList.size(); i++)
                {
                    if (id == mItemList.get(i).getId())
                    {

                        item = mItemList.get(i);

                        break;
                    }
                }

                inventoryMap.put(item.getId(), new ItemStock(item, count));


            }



            Planet planet = new Planet(planetName, distance, angle, wealth, previousValue, imports, exports, naturalResources, inventoryMap);



            return planet;

        }
        catch (JSONException e)
        {
            Log.e("ParsePlanetInfoFile", e.toString());

            return null;
        }
    }




    @Override
    protected void onHandleIntent(Intent intent)
    {
        mIsConnected = intent.getBooleanExtra("isConnected", false);

        mDirectory = intent.getStringExtra("filename");

        mNumberOfPlanets = intent.getIntExtra("numberOfPlanets", 0);

        mItemList = intent.getParcelableArrayListExtra("itemList");



        loadFromJSON();
    }


}
