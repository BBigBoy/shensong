package com.fuyang.shensong;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class MyApp extends Application {
    private static final String TAG = MyApp.class.getSimpleName();
    public static final String VISIT_URL = "http://dalab.nwafu.edu.cn/api/api.aspx";
    public static String USER_AUTH_ID;
    public static String USER_NAME;
    public static String USER_TYPE;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void exitApp() {
        MyApp.USER_TYPE = null;
        MyApp.USER_NAME = null;
        MyApp.USER_AUTH_ID = null;
    }

    /**
     * 更新接口 口令  当需要清楚的时候直接传入null
     *
     * @param key
     * @param value
     */
    public void updatePreferenceInfo(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value == null) {
            editor.remove(key).apply();
        } else {
            editor.putString(key, value).apply();
        }
    }

    /**
     * 获取键为key对应的String类型的值
     *
     * @param key
     * @return
     */
    public String getStrPreferenceInfo(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }


}
