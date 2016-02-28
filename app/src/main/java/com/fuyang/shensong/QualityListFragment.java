package com.fuyang.shensong;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.fuyang.shensong.adapter.QualityListAdapter;
import com.fuyang.shensong.extend.FJsonHttpResponseHandler;
import com.fuyang.shensong.util.BaseActivity;
import com.fuyang.shensong.util.HttpUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import medusa.theone.waterdroplistview.view.WaterDropListView;

/**
 * Created by BigBigBoy on 2015/12/4.
 */
public class QualityListFragment extends BaseFragment implements WaterDropListView.IWaterDropListViewListener {
    static final String QUALITY_LIST_CACHE_FILE_NAME = "quality-list.ch";
    private WaterDropListView waterDropListView;
    QualityListAdapter qualityListAdapter;
    private JSONArray oldQualityPageArray;
    private int pageNum = 1;

    private int regionId = 0;
    private String startTime = "", endTime = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getQualityList(true);
                    waterDropListView.stopRefresh();
                    Toast.makeText(getActivity(), "刷新成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    getQualityList(false);
                    waterDropListView.stopLoadMore();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        String qualityListStr = ((BaseActivity) getActivity()).fileRead("quality-list.ch");
        try {
            oldQualityPageArray = (JSONArray) parseStrToJSON(qualityListStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qualitylist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        waterDropListView = (WaterDropListView) view.findViewById(R.id.waterdrop_listview);
        qualityListAdapter = new QualityListAdapter(getActivity());
        waterDropListView.setAdapter(qualityListAdapter);
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        Drawable divider = getResources().getDrawable(R.drawable.divider);
        waterDropListView.setDivider(divider);
        waterDropListView.setDividerHeight(2);
        waterDropListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = QualityItemFragment.newInstance((String) view.findViewById(R.id.recordId).getTag());
                ((MainActivity) getActivity()).addRightMainFragment(fragment, "选项详情" + position);
            }
        });
        if (oldQualityPageArray != null) {
            operateQualityList(oldQualityPageArray, true);
            Log.e("QualityListFragment", oldQualityPageArray.toString());
        }
        setActionTitle("深松管家");
        getQualityList(true);
        showActionFilter(true);
        view.findViewById(R.id.action_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).addRightMainFragment(new QualityListFilterFragment(), "筛选");
            }
        });
    }

    /**
     * 拉取质量详情表。
     *
     * @param mode true为刷新，false为添加子项
     */
    void getQualityList(final Boolean mode) {
        RequestParams params = new RequestParams();
        params.put("authId", MyApp.USER_AUTH_ID);
        params.put("action", "qualitypage");
        params.put("pageSize", 20);
        if (regionId != 0) {
            params.put("regionId", regionId);
        }
        if (!startTime.equals("")) {
            params.put("from", startTime);
        }
        if (!endTime.equals("")) {
            params.put("to", endTime);
        }
        pageNum = mode ? 1 : (pageNum + 1);
        if (!mode && qualityListAdapter.getMaxId() != 0) {
            params.put("maxId", qualityListAdapter.getMaxId());
            params.put("pageNum", pageNum);
        }
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
                if (getActivity() != null)
                    ((MainActivity) getActivity()).stopProgressAnim();
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i("onSuccess", response.toString());
                    if (response.getInt("ok") == 1) {
                        JSONArray qualityPageArray = response.getJSONArray("qualitypage");
                        operateQualityList(qualityPageArray, mode);
                        ((BaseActivity) getActivity()).fileWrite("quality-list.ch", qualityPageArray.toString(), Context.MODE_PRIVATE);
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
     * 操作质量管理列表的元素
     *
     * @param mode             true为刷新，false为添加子项
     * @param qualityPageArray 质量管理列表元素的元数据数组
     */
    private void operateQualityList(JSONArray qualityPageArray, boolean mode) {
        if (mode) {
            qualityListAdapter.setDataList(new ArrayList<JSONObject>());
        }
        for (int i = 0; i < qualityPageArray.length(); i++) {
            final JSONObject qualityDetail;
            try {
                qualityDetail = qualityPageArray.getJSONObject(i);
                qualityListAdapter.addRecordInfo(qualityDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        qualityListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public void onLoadMore() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        });
    }

    /**
     * 将字符串解析为json对象
     *
     * @param jsonString
     * @return
     * @throws JSONException
     */
    private Object parseStrToJSON(String jsonString) throws JSONException {
        Object result = null;
        if (jsonString != null && !jsonString.equals("")) {
            jsonString = jsonString.trim();
            if (jsonString.startsWith("\ufeff")) {
                jsonString = jsonString.substring(1);
            }
            if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                result = (new JSONTokener(jsonString)).nextValue();
            }
        }
        return result;
    }

    public int getRegionId() {
        return regionId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
