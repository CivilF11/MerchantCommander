package com.example.zacharydeboer.merchantcommander;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zacharydeboer.merchantcommander.Model.Game;
import com.example.zacharydeboer.merchantcommander.Model.Item;
import com.example.zacharydeboer.merchantcommander.Model.Planet;
import com.example.zacharydeboer.merchantcommander.Persistence.ItemJSONHandler;
import com.example.zacharydeboer.merchantcommander.Persistence.ObjectCountJSONHandler;
import com.example.zacharydeboer.merchantcommander.Persistence.PlanetJSONHandler;

public class MainActivity extends AppCompatActivity
{

    private Button test;

    private ProgressBar mPrgLoad;




    private ObjectCountBroadcastReceiver mObjectCountReceiver;

    private Intent mObjectCountService;



    private ItemBroadcastReceiver mItemReceiver;

    private Intent mItemService;



    private PlanetBroadcastReceiver mPlanetReceiver;

    private Intent mPlanetService;



    private int mObjectsLoaded;

    private int mMaxObjects;



    private int mNumberOfPlanets;

    private int mNumberOfItems;



    private Game mGame;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mObjectsLoaded = 0;



        mGame = new Game();

        test = (Button) findViewById(R.id.test);

        test.setOnClickListener(new OnTestClickListener());



        mPrgLoad = (ProgressBar) findViewById(R.id.main_activity_prg_load);

        mPrgLoad.setProgress(0);
    }



    private void test()
    {
        mObjectsLoaded = 0;

        getObjectCount();
    }



    private boolean isConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService((CONNECTIVITY_SERVICE));

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();


    }




    private void getObjectCount()
    {
        if (isConnected())
        {
            IntentFilter filter = new IntentFilter(ObjectCountBroadcastReceiver.LOAD_RESPONSE);

            mObjectCountReceiver = new ObjectCountBroadcastReceiver();

            registerReceiver(mObjectCountReceiver, filter);



            mObjectCountService = new Intent(this, ObjectCountJSONHandler.class);

            mObjectCountService.putExtra("isConnected", isConnected());

            mObjectCountService.putExtra("filename", "objectCount.json");

            startService(mObjectCountService);

        }
        else
        {
            Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
        }
        

    }





    private final class OnTestClickListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            test();
        }
    }









    public final class ObjectCountBroadcastReceiver extends BroadcastReceiver
    {



        public static final String LOAD_RESPONSE = "OBJECT COUNT";



        @Override
        public void onReceive(Context context, Intent intent)
        {
            int[] objectCount = intent.getIntArrayExtra(ObjectCountJSONHandler.S_OBJECT_COUNT);

            mMaxObjects = 0;

            for (int objects : objectCount)
            {
                mMaxObjects += objects;
            }


            mNumberOfItems = objectCount[0];

            mNumberOfPlanets = objectCount[1];


            mPrgLoad.setMax(mMaxObjects);


            unregisterReceiver(mObjectCountReceiver);

            stopService(mObjectCountService);





            IntentFilter filter = new IntentFilter(ItemBroadcastReceiver.LOAD_RESPONSE);

            mItemReceiver = new ItemBroadcastReceiver();

            registerReceiver(mItemReceiver, filter);



            mItemService = new Intent(MainActivity.this, ItemJSONHandler.class);

            mItemService.putExtra("isConnected", isConnected());

            mItemService.putExtra("filename", "item");

            mItemService.putExtra("numberOfItems", mNumberOfItems);

            startService(mItemService);


        }
    }








    public final class ItemBroadcastReceiver extends BroadcastReceiver
    {

        public static final String LOAD_RESPONSE = "ITEM LOADED";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Item item = intent.getParcelableExtra(ItemJSONHandler.S_ITEM);

            mGame.addItem(item);

            mObjectsLoaded++;

            mPrgLoad.setProgress(mObjectsLoaded);

            System.out.println("Objects Loaded: " + mObjectsLoaded + " / " + mMaxObjects);

            if (item.getId() + 1 == mNumberOfItems)
            {
                unregisterReceiver(mItemReceiver);

                stopService(mItemService);



                IntentFilter filter = new IntentFilter(PlanetBroadcastReceiver.LOAD_RESPONSE);

                mPlanetReceiver = new PlanetBroadcastReceiver();

                registerReceiver(mPlanetReceiver, filter);



                mPlanetService = new Intent(MainActivity.this, PlanetJSONHandler.class);

                mPlanetService.putExtra("numberOfPlanets", mNumberOfPlanets);

                mPlanetService.putExtra("isConnected", isConnected());

                mPlanetService.putExtra("filename", "planet");

                mPlanetService.putParcelableArrayListExtra("itemList", mGame.getItemList());

                startService(mPlanetService);
            }
        }
    }



    public final class PlanetBroadcastReceiver extends BroadcastReceiver
    {

        public static final String LOAD_RESPONSE = "PLANET LOADED";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Planet planet = intent.getParcelableExtra(PlanetJSONHandler.S_PLANET);

            mGame.addPlanet(planet);

            mObjectsLoaded++;

            mPrgLoad.setProgress(mObjectsLoaded);

            System.out.println("Objects Loaded: " + mObjectsLoaded + " / " + mMaxObjects);


            if (mObjectsLoaded == mMaxObjects)
            {
                unregisterReceiver(mPlanetReceiver);

                stopService(mPlanetService);

                Toast.makeText(MainActivity.this, "FINAL", Toast.LENGTH_SHORT).show();

                // TODO: 2016-11-21 start game activity
            }
        }
    }



}
