package com.fuyang.shensong.extend;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;

/**
 * Created by BigBigBoy on 2015/9/10.
 */
public abstract class FJsonHttpResponseHandler extends AsyncHttpResponseHandler {
    /* 1)onStart()   方法是开始发送请求的时候执行的，一般是把progressDialog放在这里面显示，提示用户等待
　　　　2)onFailure() 如果请求失败了，就会执行该方法，其余方法就都不执行了，用于提示用户网络出现故障
　　　　3)onSuccess() 是请求成功后，服务器就会返回JSONObject，  arg1就是返回的JSONObject， 在里面解析JSONObject来回去数据，并显示在UI上
　　　　4)onFinish() 当发送请求成功后，就执行onFinish(),不会等待返回结果
   */
    private static final String LOG_TAG = "FJsonResponseHandler";

    public FJsonHttpResponseHandler() {
        this("UTF-8");
    }

    public FJsonHttpResponseHandler(String encoding) {
        this.setCharset(encoding);
    }

    /**
     * 请求成功，并且请求结果成功解析为Json对象
     *
     * @param statusCode int
     * @param headers    Header[]
     * @param response   JSONObject
     */
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.i("FJsonResponseHandler", "onSuccess(int statusCode, Header[] headers, JSONObject response)");
    }

    /**
     * 请求成功，并且请求结果成功解析为JsonArray对象
     *
     * @param statusCode int
     * @param headers    Header[]
     * @param response   JSONArray
     */
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.i("FJsonResponseHandler", "onSuccess(int statusCode, Header[] headers, JSONArray response)");
    }

    /**
     * 请求成功，但请求结果未成功解析
     *
     * @param statusCode    int
     * @param headers       Header[]
     * @param responseBytes byte[]
     */
    public void onSuccessButResponseContentErr(int statusCode, Header[] headers, final byte[] responseBytes) {
        Log.i("FJsonResponseHandler", "onSuccessButResponseContentErr(int statusCode, Header[] headers, final byte[] responseBytes)");
    }

    public final void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBytes) {
        if (statusCode != 204) {
            Runnable parser = new Runnable() {
                public void run() {
                    try {
                        final Object ex = FJsonHttpResponseHandler.this.parseResponse(responseBytes);
                        FJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                if (ex instanceof JSONObject) {
                                    FJsonHttpResponseHandler.this.onSuccess(statusCode, headers, (JSONObject) ex);
                                } else if (ex instanceof JSONArray) {
                                    FJsonHttpResponseHandler.this.onSuccess(statusCode, headers, (JSONArray) ex);
                                } else {
                                    FJsonHttpResponseHandler.this.onSuccessButResponseContentErr(statusCode, headers, responseBytes);
                                }
                            }
                        });
                    } catch (final JSONException var2) {
                        FJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                FJsonHttpResponseHandler.this.onSuccessButResponseContentErr(statusCode, headers, responseBytes);
                            }
                        });
                    }

                }
            };
            if (!this.getUseSynchronousMode()) {
                (new Thread(parser)).start();
            } else {
                parser.run();
            }
        } else {
            this.onSuccess(statusCode, headers, new JSONObject());
        }

    }

    /**
     * http请求失败
     *
     * @param i         int
     * @param headers   Header[]
     * @param bytes     byte[]
     * @param throwable Throwable
     */
    @Override
    public abstract void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable);

    protected Object parseResponse(byte[] responseBody) throws JSONException {
        if (null == responseBody) {
            return null;
        } else {
            Object result = null;
            String jsonString = getResponseString(responseBody, this.getCharset());
            if (jsonString != null) {
                jsonString = jsonString.trim();
                if (jsonString.startsWith("\ufeff")) {
                    jsonString = jsonString.substring(1);
                }
                if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                    result = (new JSONTokener(jsonString)).nextValue();
                }
            }
            if (result == null) {
                result = jsonString;
            }
            return result;
        }
    }

    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            String e = stringBytes == null ? null : new String(stringBytes, charset);
            return e != null && e.startsWith("\ufeff") ? e.substring(1) : e;
        } catch (UnsupportedEncodingException var3) {
            Log.i("FJsonResponseHandler", "Encoding response into string failed", var3);
            return null;
        }
    }
}
