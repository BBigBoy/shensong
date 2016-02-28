package com.fuyang.shensong.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.fuyang.shensong.MyApp;
import com.fuyang.shensong.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by BigBigBoy on 2015/12/6.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.main);//通知栏所需颜色
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 读取应用文件内容
     *
     * @param name
     * @return
     */
    public String fileRead(String name) {
        name = MyApp.USER_NAME + name;
        char[] inputBuffer = new char[255];
        int length;
        StringBuilder data = new StringBuilder();
        try {
            FileInputStream fIn = openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fIn);
            while ((length = isr.read(inputBuffer)) != -1) {
                data = data.append(String.valueOf(inputBuffer, 0, length));
            }
            isr.close();
            fIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    /**
     * 写内容到应用文件存储区
     *
     * @param name
     * @param data
     * @param mode
     */
    public void fileWrite(String name, String data, int mode) {
        name = MyApp.USER_NAME + name;
        try {
            FileOutputStream fOut = openFileOutput(name, mode);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除应用文件存储区文件
     *
     * @param name
     */
    public void fileDel(String name) {
        deleteFile(MyApp.USER_NAME + name);
    }
}
