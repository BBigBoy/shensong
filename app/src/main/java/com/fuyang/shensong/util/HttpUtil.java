package com.fuyang.shensong.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();    //实例话对象

    static {
        client.setTimeout(11000);   //设置链接超时，如果不设置，默认为10s
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)   //url里面带参数
    {
        client.get(url, params, responseHandler);
    }

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler)   //带参数，获取json对象或者数组
    {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)   //url里面带参数
    {
        client.post(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler)   //带参数，获取json对象或者数组
    {
        client.post(url, params, responseHandler);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }
}