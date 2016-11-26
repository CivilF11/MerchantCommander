package com.example.zacharydeboer.merchantcommander.Persistence;



public interface IJSONHandler
{



    String URL = "http://mobile.sheridanc.on.ca/~deboerz/MerchantCommander/";





    void loadFromJSON();

    Object parseJSONFile(String jsonData);







}
