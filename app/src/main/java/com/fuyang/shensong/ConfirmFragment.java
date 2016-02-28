package com.fuyang.shensong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fuyang.shensong.extend.FJsonHttpResponseHandler;
import com.fuyang.shensong.util.BaseActivity;
import com.fuyang.shensong.util.HttpUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by BigBigBoy on 2015/12/8.
 */
public class ConfirmFragment extends Fragment {
    public static boolean isShowing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //如果校验口令失效，则清除登录信息
        long authTime = Long.valueOf(((MyApp) getActivity().getApplication()).getStrPreferenceInfo("auth_time"));
        if ((System.currentTimeMillis() - authTime) > 3600 * 24 * 30) {
            logOut();
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        isShowing = true;
        return inflater.inflate(R.layout.fragment_confirm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnClear = (Button) view.findViewById(R.id.btnClear);
        Button btnExit = (Button) view.findViewById(R.id.btnExit);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
                getActivity().getSupportFragmentManager().popBackStack();
                ((MainActivity) getActivity()).addFragment(R.id.activity_main, new LoginFragment(), "login");
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * 注销
     */

    private void logOut() {
        ((MyApp) getActivity().getApplication()).updatePreferenceInfo("login_authId", null);
        ((MyApp) getActivity().getApplication()).updatePreferenceInfo("login_name", null);
        ((MyApp) getActivity().getApplication()).updatePreferenceInfo("login_type", null);
        ((MyApp) getActivity().getApplication()).updatePreferenceInfo("auth_time", null);
        ((BaseActivity) getActivity()).fileDel(QualityListFragment.QUALITY_LIST_CACHE_FILE_NAME);
        MyApp.exitApp();
        logServerOut();
    }

    /**
     * 清除服务器端登陆信息
     */
    private void logServerOut() {
        RequestParams params = new RequestParams();
        params.put("authId", MyApp.USER_AUTH_ID);
        params.put("action", "logout");
        HttpUtil.post(MyApp.VISIT_URL, params, new FJsonHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        isShowing = false;
        super.onDestroyView();
    }
}
