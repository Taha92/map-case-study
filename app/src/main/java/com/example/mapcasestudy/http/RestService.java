package com.example.mapcasestudy.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.mapcasestudy.interfaces.IWebServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestService {

    private static IWebServices service;
    private static IWebServices serviceSync;
    private static OkHttpClient httpClient;
    private static OkHttpClient httpSyncClient;

    //region Helper methods for Retrofit default
    public static void initializeHttpService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtils.BASE_URL)
                .client(getDefaultHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        if (service == null) {
            service = retrofit.create(IWebServices.class);
        }

    }

    public static OkHttpClient getDefaultHttpClient() {

        if (httpClient == null) {

            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(HttpUtils.READ_TIME_OUT, TimeUnit.SECONDS);
            b.connectTimeout(HttpUtils.CONNECTION_TIME_OUT, TimeUnit.SECONDS);

            b.interceptors().add(new LoggingInterceptor());
            b.interceptors().add(new RequestInterceptor());

            httpClient = b.build();
        }

        return httpClient;
    }

    public static IWebServices getHttpService() {

        if (service == null) {
            throw new HttpServiceNotInitialized();
        }

        return service;
    }

    //endregion

    //region Helper method for Retrofit Sync
    public static void initializeSyncHttpService() {

        Gson gson = new GsonBuilder()
                .setLenient()
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofitSync = new Retrofit.Builder()
                .baseUrl(HttpUtils.BASE_URL)
                .client(getSyncHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        if (serviceSync == null) {
            serviceSync = retrofitSync.create(IWebServices.class);
        }

    }

    private static OkHttpClient getSyncHttpClient() {
        if (httpSyncClient == null) {
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.readTimeout(HttpUtils.READ_TIME_OUT, TimeUnit.SECONDS);
            b.connectTimeout(HttpUtils.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
            b.interceptors().add(new LoggingInterceptor());
            b.interceptors().add(new SyncRequestInterceptor());
            httpSyncClient = b.build();
        }
        return httpSyncClient;
    }

    /*public static iWebServices getSyncHttpService() {
        if (serviceSync == null) {
            throw new SyncHttpServiceNotInitialized();
        }
        return serviceSync;
    }*/
    //endregion


    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
