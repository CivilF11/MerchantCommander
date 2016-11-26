package com.example.zacharydeboer.merchantcommander.Persistence;


import android.app.IntentService;
import android.content.Intent;
import android.os.Parcel;

import com.example.zacharydeboer.merchantcommander.MainActivity;
import com.example.zacharydeboer.merchantcommander.Model.Item;
import com.example.zacharydeboer.merchantcommander.Model.Planet;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class ItemJSONHandler extends IntentService implements IJSONHandler
{


    public final static String S_ITEM = "ITEM";


    private String mDirectory;

    private boolean mIsConnected;

    private Item mItem;

    private int mNumberOfItems;



    public ItemJSONHandler()
    {
        super("ItemJSONHandler");
    }



    @Override
    public void loadFromJSON()
    {

        for (int i = 0; i < mNumberOfItems; i++)
        {
            if (mIsConnected)
            {

                try
                {
                    InputStream inputStream;

                    java.net.URL url = new URL(URL + mDirectory + i + ".json");

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

                        mItem = parseJSONFile(stringBuilder.toString());



                        Intent broadcastIntent = new Intent();

                        broadcastIntent.setAction(MainActivity.ItemBroadcastReceiver.LOAD_RESPONSE);

                        broadcastIntent.putExtra(S_ITEM, mItem);

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
                // TODO: 2016-11-16 Implement error catch, no connection
            }

        }


    }



    @Override
    public Item parseJSONFile(String jsonData)
    {
        try
        {
            JSONObject wrapper = new JSONObject(jsonData);



            int id = Integer.parseInt(wrapper.getString("id"));

            String name = wrapper.getString("name");

            int baseValue = Integer.parseInt(wrapper.getString("baseValue"));

            return new Item(id, name, baseValue);

        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }





    @Override
    protected void onHandleIntent(Intent intent)
    {
        mIsConnected = intent.getBooleanExtra("isConnected", false);

        mDirectory = intent.getStringExtra("filename");

        mNumberOfItems = intent.getIntExtra("numberOfItems", 0);

        loadFromJSON();
    }
}
