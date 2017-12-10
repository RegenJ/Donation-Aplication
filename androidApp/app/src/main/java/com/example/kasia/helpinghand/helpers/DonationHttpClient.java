package com.example.kasia.helpinghand.helpers;



import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Singleton client class, client should not be initialised several times
 */


public class DonationHttpClient extends OkHttpClient{

    private static OkHttpClient clientInstance = null;


    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected DonationHttpClient() {

    }

    public static OkHttpClient getInstance() {
        if (clientInstance == null) {
            clientInstance  = new OkHttpClient();
        }
        return clientInstance;
    }

    //send post request do server
    public static String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = clientInstance.newCall(request).execute();
        return response.body().string();
    }

    //send get request do server
    public static String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = clientInstance.newCall(request).execute();
        return response.body().string();
    }




}
