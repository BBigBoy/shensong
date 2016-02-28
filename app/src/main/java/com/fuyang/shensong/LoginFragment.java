package com.fuyang.shensong;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyang.shensong.extend.FJsonHttpResponseHandler;
import com.fuyang.shensong.util.HttpUtil;
import com.fuyang.shensong.util.RSA;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BigBigBoy on 2015/9/10.
 */
public class LoginFragment extends Fragment {
    AppCompatCheckBox rememberPwd;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyApp.USER_TYPE = ((MyApp) activity.getApplication()).getStrPreferenceInfo("login_type");
        MyApp.USER_NAME = ((MyApp) activity.getApplication()).getStrPreferenceInfo("login_name");
        MyApp.USER_AUTH_ID = ((MyApp) activity.getApplication()).getStrPreferenceInfo("login_authId");
        String authTimeStr = ((MyApp) activity.getApplication()).getStrPreferenceInfo("auth_time");
        if (authTimeStr != null) {
            long authTime = Long.valueOf(authTimeStr);
            if (((System.currentTimeMillis() - authTime) < 3600 * 24 * 30)) {
                if ((MyApp.USER_AUTH_ID != null) && (MyApp.USER_NAME != null) && (MyApp.USER_TYPE != null)) {
                    ((MainActivity) activity).initFragment();
                    ((MainActivity) activity).getSupportFragmentManager()
                            .beginTransaction().remove(LoginFragment.this).commit();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).hideBottom();
        initView(view);
    }

    private void initView(View view) {
        final TextView userName = (EditText) view.findViewById(R.id.username);
        final TextView userPwd = (EditText) view.findViewById(R.id.pwd);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        rememberPwd = (AppCompatCheckBox) view.findViewById(R.id.rem_pwd);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = String.valueOf(userName.getText());
                String user_pwd = String.valueOf(userPwd.getText());
                if (user_name.trim().equals("") || user_pwd.trim().equals("")) {
                    displayToast("请输入账号及密码");
                    return;
                }
                getUserManageInfo(user_name, user_pwd);
            }
        });
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getActivity()).showBottom();
        super.onDestroyView();
    }

    private void getUserManageInfo(String user_name, String user_pwd) {
        RequestParams params = new RequestParams();
        String encryptRsaStr = null;
        try {
            encryptRsaStr = RSA.encrypt((user_name + "|" + user_pwd).getBytes("utf-8"), RSA.getPublicKey(RSA.modulus, RSA.exponent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("user", encryptRsaStr);
        params.put("model", 2);
        params.put("action", "login");
        params.put("sdk", 5);
        params.put("appver", 22);
        HttpUtil.post(MyApp.VISIT_URL, params, new FJsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d("onStart", "onStart");
                ((MainActivity) getActivity()).showProgressAnim();
                super.onStart();
            }

            @Override
            public void onFinish() {
                Log.d("onFinish", "onFinish");
                ((MainActivity) getActivity()).stopProgressAnim();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getString("ok").equals("1")) {
                        JSONObject loginInfo = response.getJSONObject("login");
                        MyApp myAppContext = ((MyApp) getActivity().getApplication());
                        MyApp.USER_AUTH_ID = loginInfo.getString("authId");
                        MyApp.USER_NAME = loginInfo.getString("name");
                        MyApp.USER_TYPE = String.valueOf(loginInfo.getInt("type"));
                        if (rememberPwd.isChecked()) {
                            myAppContext.updatePreferenceInfo("login_type", String.valueOf(loginInfo.getInt("type")));
                            myAppContext.updatePreferenceInfo("login_name", String.valueOf(loginInfo.getString("name")));
                            myAppContext.updatePreferenceInfo("login_authId", String.valueOf(loginInfo.getString("authId")));
                            myAppContext.updatePreferenceInfo("auth_time", String.valueOf(System.currentTimeMillis()));
                        }
                        ((MainActivity) getActivity()).initFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().remove(LoginFragment.this).commit();
                    } else if (response.getString("ok").equals("0")) {
                        displayToast("账号或密码错误！");
                    }
                } catch (JSONException e) {
                    displayToast("系统维护，请稍后再试！");
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessButResponseContentErr(int statusCode, Header[] headers, byte[] responseBytes) {
                Log.d("onResponseContentErr", "request success,but the content is wrong!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.d("onFailure", "request success,but the content is wrong!");
                displayToast("网络连接失败，请检查网络后重试！");
            }
        });
    }

    private void displayToast(final String toastContent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_LONG).show();
            }
        });
    }
}
