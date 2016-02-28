package com.fuyang.shensong;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuyang.shensong.extend.FJsonHttpResponseHandler;
import com.fuyang.shensong.util.HttpUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by BigBigBoy on 2015/12/4.
 */
public class DeviceInfoFragment extends BaseFragment {

    private int deviceId;

    public static DeviceInfoFragment newInstance(int deviceId) {
        DeviceInfoFragment deviceInfoFragment = new DeviceInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("deviceId", deviceId);
        deviceInfoFragment.setArguments(args);
        return deviceInfoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Serializable seriInfo = getArguments().getSerializable("deviceId");
        if (seriInfo != null) {
            deviceId = (int) seriInfo;
        } else {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setErrText("出现错误！");
            errorDialog.show(getFragmentManager(), "ErrorDialog");
        }
        View view = inflater.inflate(R.layout.fragment_deviceinfo, container, false);
        getDeviceInfo(deviceId, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showActionBack(true);
        setActionTitle("设备信息");
    }

    private void initView(View view, JSONObject deviceObj) {
        TextView deviceId = (TextView) view.findViewById(R.id.device_id);
        TextView regDate = (TextView) view.findViewById(R.id.reg_date);
        TextView regRegion = (TextView) view.findViewById(R.id.reg_region);
        TextView telNum = (TextView) view.findViewById(R.id.tel_num);
        TextView vehicleGrade = (TextView) view.findViewById(R.id.vehicle_grade);
//        TextView curPosition = (TextView) view.findViewById(R.id.cur_position);
        TextView deviceStatus = (TextView) view.findViewById(R.id.device_status);
        TextView lastActiveTime = (TextView) view.findViewById(R.id.last_active_time);
        TextView regStatus = (TextView) view.findViewById(R.id.reg_status);
        try {
            deviceId.setText(deviceObj.getInt("id") + "号");
            regDate.setText(deviceObj.getString("registerTime"));
            regRegion.setText(deviceObj.getString("regionName"));
            telNum.setText(deviceObj.getString("phone"));
            vehicleGrade.setText(deviceObj.getString("no"));
//            curPosition.setText(deviceObj.getString("id"));
            deviceStatus.setText(getDeviceStatus(deviceObj.getInt("status")));
            lastActiveTime.setText(deviceObj.getString("time"));
            regStatus.setText(deviceObj.getBoolean("isRegistered") ? "已注册" : "未注册");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getDeviceStatus(int deviceState) {
        String deviceStateStr;
        switch (deviceState) {
            case 1:
                deviceStateStr = "作业中";
                break;
            case 2:
                deviceStateStr = "作业异常";
                break;
            case 3:
                deviceStateStr = "行进中";
                break;
            case 4:
                deviceStateStr = "停止";
                break;
            case 5:
                deviceStateStr = "故障";
                break;
            default:
                deviceStateStr = "未知状态";
        }
        return deviceStateStr;
    }

    /**
     * 获取该次操作质量详情
     */
    private void getDeviceInfo(int devicedId, final View view) {
        RequestParams params = new RequestParams();
        params.put("authId", MyApp.USER_AUTH_ID);
        params.put("action", "devicedetail");
        params.put("deviceId", devicedId);
        HttpUtil.post(MyApp.VISIT_URL, params, new FJsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d("onStart", "onStart");
                if (getActivity() != null)
                    ((MainActivity) getActivity()).showProgressAnim();
                super.onStart();
            }

            @Override
            public void onFinish() {
                Log.d("onFinish", "onFinish");
                if (getActivity() != null)
                    ((MainActivity) getActivity()).stopProgressAnim();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i("onSuccess", response.toString());
                    if (response.getInt("ok") == 1) {
                        JSONObject baseJsonObj = response.getJSONObject("devicedetail");
                        initView(view, baseJsonObj);
                    } else if (response.getInt("ok") == 0) {
                    }
                } catch (JSONException e) {
                    //displayToast("系统维护，请稍后再试！");
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
//                displayToast("网络连接失败，请检查网络后重试！");
            }
        });
    }
}