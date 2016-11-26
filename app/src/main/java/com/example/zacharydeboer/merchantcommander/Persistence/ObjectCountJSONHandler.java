package com.example.zacharydeboer.merchantcommander.Persistence;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.zacharydeboer.merchantcommander.MainActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class ObjectCountJSONHandler extends IntentService implements IJSONHandler
{


    public final static String S_OBJECT_COUNT = "OBJECT COUNT";


    private String mDirectory;

    private boolean mIsConnected;

    private int[] mObjectCount;




    public ObjectCountJSONHandler()
    {
        super("ObjectCountJSONHandler");
    }

    @Override
    public void loadFromJSON()
    {
        if (mIsConnected)
        {
            

            try
            {
                InputStream inputStream;

                java.net.URL url = new URL(URL + mDirectory);

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

                    mObjectCount = parseJSONFile(stringBuilder.toString());



                    Intent broadcastIntent = new Intent();

                    broadcastIntent.putExtra(S_OBJECT_COUNT, mObjectCount);

                    broadcastIntent.setAction(MainActivity.ObjectCountBroadcastReceiver.LOAD_RESPONSE);

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
            Log.e("ObjectCountJSONHandler", "No Connection");
        }


    }






    @Override
    public int[] parseJSONFile(String jsonData)
    {
        try
        {
            JSONObject wrapper = new JSONObject(jsonData);

            int[] objectCount = new int[2];

            objectCount[0] = Integer.parseInt(wrapper.getString("numberOfItems"));

            objectCount[1] = Integer.parseInt(wrapper.getString("numberOfPlanets"));


            return objectCount;

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

        loadFromJSON();
    }
}
