package com.jiajun.demo.network;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jiajun.demo.network.api.LoginApi;
import com.jiajun.demo.network.api.NewsApi;
import com.jiajun.demo.network.api.TestApi;
import com.jiajun.demo.network.api.WechatApi;
import com.jiajun.demo.util.IMSI;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * api
 */


public class Network {

    public static final String ROOT_URL = "http://v.juhe.cn/";

    private static TestApi testApi;
    private static LoginApi loginApi;
    private static NewsApi sNewsApi;
    private static WechatApi mWechatApi;



    private static OkHttpClient okHttpClient ;
    private static Retrofit retrofit;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    public Network(){

    }
    public static void  init(Context context) {
        Map<String,Object> map = new HashMap<>();
        map.put("platform","2");
        map.put("imsi", IMSI.getImsi(context));
        NetworkInterceptor interceptor = new NetworkInterceptor(map);
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor()).build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://www.implus100.com/agent/interface/")
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    public static TestApi getVersionApi() {
        if (testApi == null) {
            testApi = retrofit.create(TestApi.class);
        }
        Log.e("oooooo","getVersionApi");
        return testApi;
    }


    public static LoginApi login() {
        if (loginApi == null) {
            loginApi = retrofit.create(LoginApi.class);
        }
        Log.e("oooooo","login");
        return loginApi;
    }



    public static NewsApi getNewsApi() {
        if (sNewsApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            sNewsApi = retrofit.create(NewsApi.class);
        }
        Log.e("oooooo","getNewsApi");
        return sNewsApi;
    }

    public static WechatApi getWechatApi() {
        if (mWechatApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            mWechatApi = retrofit.create(WechatApi.class);
        }
        Log.e("oooooo","getwechatApi");
        return mWechatApi;
    }


}
