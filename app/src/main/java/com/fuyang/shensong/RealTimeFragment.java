package com.fuyang.shensong;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.fuyang.shensong.extend.FJsonHttpResponseHandler;
import com.fuyang.shensong.util.HttpUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RealTimeFragment extends BaseFragment {
    private static final String TAG = RealTimeFragment.class.getSimpleName();
    // 地图相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor[] markerIcon = new BitmapDescriptor[4];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realtime, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @SuppressWarnings("ALL")
    private void initView(View view) {
        markerIcon[0] = BitmapDescriptorFactory
                .fromResource(R.drawable.dot1);
        markerIcon[1] = BitmapDescriptorFactory
                .fromResource(R.drawable.dot2);
        markerIcon[2] = BitmapDescriptorFactory
                .fromResource(R.drawable.dot3);
        markerIcon[3] = BitmapDescriptorFactory
                .fromResource(R.drawable.dot4);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setBuildingsEnabled(true);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                InfoWindow mInfoWindow;
                String showStr = "";
                int status = marker.getExtraInfo().getInt("status");
                switch (status) {
                    case 1:
                        showStr = "该深松机正在作业！";
                        break;
                    case 2:
                        showStr = "该深松机作业出现异常！";
                        break;
                    case 3:
                        showStr = "该深松机正在行进中！";
                        break;
                    case 4:
                        showStr = "该深松机处于停止状态！";
                        break;
                    case 5:
                        showStr = "该深松机出现故障！";
                        break;
                }
                //生成一个TextView用户在地图中显示InfoWindow
                final TextView infoWindowContent = new TextView(getActivity().getApplicationContext());
                infoWindowContent.setBackgroundResource(R.drawable.newsbg);
                infoWindowContent.setPadding(20, 20, 20, 20);
                infoWindowContent.setText(showStr);
                infoWindowContent.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
                infoWindowContent.setTextSize(17);
                infoWindowContent.setTextColor(Color.rgb(255, 255, 255));
                infoWindowContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });
                //将marker所在的经纬度的信息转化成屏幕上的坐标
                final LatLng ll = marker.getPosition();
                //为弹出的InfoWindow添加点击事件
                mInfoWindow = new InfoWindow(infoWindowContent, ll, -16);
                //显示InfoWindow
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
        view.findViewById(R.id.action_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).addLeftMainFragment(new RealTimeFilterFragment(), "筛选");
            }
        });
        // 界面加载时添加绘制图层
        getRealTimeStatus(0, 0, 0);
        showActionFilter(true);
    }

    void getRealTimeStatus(final int status, final int regionId, final int bycurpos) {
        RequestParams params = new RequestParams();
        params.put("authId", MyApp.USER_AUTH_ID);
        params.put("action", "realtimestatus");
        params.put("status", status);
        params.put("regionId", regionId);
        params.put("bycurpos", bycurpos);
        HttpUtil.post(MyApp.VISIT_URL, params, new FJsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d("HttpUtil.postonStart", "status:" + status + "\nregionId:" + regionId + "\nbycurpos:" + bycurpos);
                if (getActivity() != null)
                    ((MainActivity) getActivity()).showProgressAnim();
                super.onStart();
            }

            @Override
            public void onFinish() {
                Log.d("HttpUtil.postonFinish", "status:" + status + "\nregionId:" + regionId + "\nbycurpos:" + bycurpos);
                if (getActivity() != null)
                    ((MainActivity) getActivity()).stopProgressAnim();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i("HttpUtil.postonSuccess", response.toString());
                    if (response.getInt("ok") == 1) {
                        Log.i("realtimestatus", response.toString());
                        mBaiduMap.clear();
                        JSONArray realTimeStatusArray = response.getJSONArray("realtimestatus");
                        int deviceNum = realTimeStatusArray.length();
                        if (deviceNum == 0) {
                            displayToast("当前区域不存在设备！");
                        } else {
                            for (int i = 0; i < deviceNum; i++) {
                                JSONObject deviceInfoObj = ((JSONObject) realTimeStatusArray.get(i));
                                try {
                                    int status = deviceInfoObj.getInt("status");
                                    LatLng point = new LatLng(deviceInfoObj.getDouble("lat"), deviceInfoObj.getDouble("lng"));
                                    addMarker(point, status);
                                    if (i == 0) {
                                        //定义地图状态
                                        MapStatus mMapStatus = new MapStatus.Builder()
                                                .target(point).zoom(16).build();
                                        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                                        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                                        //改变地图状态
                                        mBaiduMap.setMapStatus(mMapStatusUpdate);
                                    }
                                } catch (JSONException e) {
                                    //当无法获取设备任何有效数据时，status返回0，且不返回lng、lat和course值。
                                    //所以此处捕获一下，保证不会因为一个无效数据影响全部
                                    e.printStackTrace();
                                }
                            }
                        }
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

    /**
     * 添加一个位置标识
     */
    public void addMarker(LatLng point, int status) {
        int index = (status == 5) ? (1) : (status - 1);
        MarkerOptions ooA = new MarkerOptions().position(point)
                .icon(markerIcon[index]).anchor(0.5f, 0.5f);
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        ooA.extraInfo(bundle);
        mBaiduMap.addOverlay(ooA);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        for (BitmapDescriptor aMarkerIcon : markerIcon) {
            aMarkerIcon.recycle();
        }
        super.onDestroy();
    }

}

