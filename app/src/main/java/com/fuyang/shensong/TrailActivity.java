package com.fuyang.shensong;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.fuyang.shensong.util.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class TrailActivity extends BaseActivity {
    private static final String TAG = TrailActivity.class.getSimpleName();
    // 地图相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    Polyline mPolyline;
    List<LatLng> points = new ArrayList<>();
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor markerIcon;
    Marker trailMaker;
    Thread reviewTrailThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplication());
        setContentView(R.layout.fragment_trail);
        String pointsStr = getIntent().getStringExtra("pos");
        if (pointsStr != null) {
            String[] pointArr = pointsStr.split(",");
            for (int i = 0; i < pointArr.length; ) {
                points.add(new LatLng(Double.parseDouble(pointArr[i + 1]), Double.parseDouble(pointArr[i])));
                i = i + 2;
            }
        }
        initView();
    }

    @SuppressWarnings("ALL")
    private void initView() {
        markerIcon = BitmapDescriptorFactory
                .fromResource(R.drawable.marker);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setBuildingsEnabled(true);
        MarkerOptions ooA = new MarkerOptions().position(points.get(0))
                .icon(markerIcon).anchor(0.5f, 0.5f);
        trailMaker = (Marker) (mBaiduMap.addOverlay(ooA));
        // 界面加载时添加绘制图层
        addCustomElementsDemo();
        ((TextView) findViewById(R.id.action_title)).setText("作业轨迹");
        findViewById(R.id.action_back).setVisibility(View.VISIBLE);
        findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.slide_right_out);
            }
        });
        findViewById(R.id.review_trail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runRoute();
            }
        });
    }

    private void runRoute() {
        if (reviewTrailThread != null) {
            return;
        }
        reviewTrailThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (LatLng point : points) {
                            trailMaker.setPosition(point);
                            try {
                                if (points.size() > 60) {
                                    Thread.sleep(8);
                                } else {
                                    Thread.sleep(20);
                                }
                            } catch (InterruptedException e) {
                                Log.d("InterruptedException", "InterruptedException");
                                return;
                            }
                        }
                        trailMaker.setPosition(points.get(1));
                        reviewTrailThread = null;
                    }
                });
        reviewTrailThread.start();
    }

    /**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElementsDemo() {
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(points.get(0)).zoom(22).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //设置地图类型为卫星地图
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        OverlayOptions ooPolyline = new PolylineOptions().width(8)
                .color(0xAAFF0000).points(points);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
    }

    @Override
    protected void onPause() {
        if (reviewTrailThread != null) {
            reviewTrailThread.interrupt();
            try {
                Log.d("before", "before");
                Thread.sleep(30);
                Log.d("later", "later");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        markerIcon.recycle();
        super.onDestroy();
    }

}

