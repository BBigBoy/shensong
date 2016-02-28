package com.fuyang.shensong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyang.shensong.extend.FJsonHttpResponseHandler;
import com.fuyang.shensong.util.HttpUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by BigBigBoy on 2015/12/4.
 */
public class QualityItemFragment extends BaseFragment {
    private ColumnChartView chart;
    private JSONObject baseJsonObj;
    /**
     * 记录轨迹坐标点
     */
    private String pos;

    public static QualityItemFragment newInstance(String itemBaseObjStr) {
        QualityItemFragment qualityFragment = new QualityItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemBaseObjStr", itemBaseObjStr);
        qualityFragment.setArguments(args);
        return qualityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Serializable seriInfo = getArguments().getSerializable("itemBaseObjStr");
        if (seriInfo != null && seriInfo instanceof String) {
            try {
                baseJsonObj = new JSONObject((String) seriInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setErrText("出现错误！");
            errorDialog.show(getFragmentManager(), "ErrorDialog");
        }
        View view = inflater.inflate(R.layout.fragment_qulityitemdetail, container, false);
        initView(view);
        try {
            getDetailInfo(baseJsonObj.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View view) {
        chart = (ColumnChartView) view.findViewById(R.id.column_chart_ciew);
        TextView recordId = (TextView) view.findViewById(R.id.recordId);
        TextView deviceId = (TextView) view.findViewById(R.id.device_id);
        TextView workProp = (TextView) view.findViewById(R.id.work_prop);
        TextView workStartTime = (TextView) view.findViewById(R.id.work_start_time);
        TextView workEndTime = (TextView) view.findViewById(R.id.work_end_time);
        TextView manner = (TextView) view.findViewById(R.id.manner);
        TextView meanDepth = (TextView) view.findViewById(R.id.mean_depth);
        TextView operArea = (TextView) view.findViewById(R.id.oper_area);
        TextView complianceRate = (TextView) view.findViewById(R.id.compliance_rate);
        TextView workLength = (TextView) view.findViewById(R.id.work_length);
        try {
            recordId.setText(String.valueOf(baseJsonObj.getInt("id")) + "号");
            workProp.setText(String.valueOf(baseJsonObj.getString("area")) + "平方米");
            workStartTime.setText(String.valueOf(baseJsonObj.getString("start")));
            workEndTime.setText(String.valueOf(baseJsonObj.getString("end")));
            manner.setText(String.valueOf(baseJsonObj.getString("contact")));
            meanDepth.setText(String.valueOf(baseJsonObj.getString("averDepth")) + "厘米");
            operArea.setText(String.valueOf(baseJsonObj.getString("regionName")));
            complianceRate.setText(String.valueOf(baseJsonObj.getString("valid")) + "%");
            workLength.setText(String.valueOf(baseJsonObj.getString("mileage")) + "米");
            deviceId.setText(String.valueOf(baseJsonObj.getString("deviceId")) + " 号设备");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View deviceInfo = view.findViewById(R.id.device_info);
        deviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((MainActivity) getActivity()).addRightMainFragment(
                            DeviceInfoFragment.newInstance(Integer.valueOf(baseJsonObj.getString("deviceId"))),
                            "DeviceInfoFragment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        View workTrail = view.findViewById(R.id.work_trail);
        workTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos == null){
                    displayToast("当前网络不佳，未同步到轨迹数据！");
                    return;
                }
                //百度地图在fragment中会闪屏，所以就使用activity吧
                Intent trailIntent = new Intent(getActivity(), TrailActivity.class);
                if (pos != null && pos.length() > 3) {
                    trailIntent.putExtra("pos", pos.substring(1, pos.length() - 1));
                }
                startActivity(trailIntent);
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_static);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showActionBack(true);
        setActionTitle("质量详情");
    }

    /**
     * 获取该次操作质量详情
     */
    private void getDetailInfo(int wordId) {
        RequestParams params = new RequestParams();
        params.put("authId", MyApp.USER_AUTH_ID);
        params.put("action", "qualitydetail");
        params.put("workId", wordId);
        params.put("pos", 1);
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
                        JSONObject baseJsonObj = response.getJSONObject("qualitydetail");
                        JSONArray depthArray = baseJsonObj.getJSONArray("depth");
                        JSONArray posArray = baseJsonObj.getJSONArray("pos");
                        pos = posArray.toString();
                        initChartView(depthArray);
                        Log.i("depthArray", depthArray.toString());
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

    private void initChartView(JSONArray depthArray) {
        int numSubcolumns = 1;
        int numColumns = depthArray.length();
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {
                try {
                    values.add(new SubcolumnValue((float) depthArray.getJSONObject(i).getDouble("v"), ChartUtils.pickColor()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }
        ColumnChartData data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("深度分布");
        axisY.setName("作业量");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        chart.setColumnChartData(data);
        chart.setOnValueTouchListener(new ValueTouchListener(depthArray));
    }


    private class ValueTouchListener implements ColumnChartOnValueSelectListener {
        JSONArray depthArray;

        public ValueTouchListener(JSONArray depthArray) {
            this.depthArray = depthArray;
        }

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            try {
                Toast.makeText(getActivity(), depthArray.getJSONObject(columnIndex).getString("t"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }
    }
}