package com.fuyang.shensong.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fuyang.shensong.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by BigBigBoy on 2015/12/4.
 */
public class QualityListAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> dataList = new ArrayList<>();


    public QualityListAdapter(Context context) {
        this.context = context;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来改变数据集。
     * 仍需手动调用notifyDataSetChanged刷新ListView
     *
     * @param list
     */
    public void setDataList(List<JSONObject> list) {
        this.dataList = list;
    }

    /**
     * 获得ListView中id最大的一个ID值
     *
     * @return
     */
    public int getMaxId() {
        if (this.dataList.size() == 0) {
            return 0;
        }
        try {
            return this.dataList.get(0).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param recordInfo
     */
    public void addRecordInfo(JSONObject recordInfo) {
        dataList.add(recordInfo);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.quality_item, null, false);
        }
        JSONObject qualityDetail = dataList.get(position);
        ViewHolder mViewHolder = ViewHolder.get(convertView);
        TextView recordId = mViewHolder.getView(R.id.recordId);
        TextView workProp = mViewHolder.getView(R.id.work_prop);
        TextView workTime = mViewHolder.getView(R.id.work_time);
        TextView manner = mViewHolder.getView(R.id.manner);
        TextView meanDepth = mViewHolder.getView(R.id.mean_depth);
        TextView operArea = mViewHolder.getView(R.id.oper_area);
        TextView complianceRate = mViewHolder.getView(R.id.compliance_rate);
        try {
            recordId.setText(String.valueOf(qualityDetail.getInt("id")));
            recordId.setTag(qualityDetail.toString());
            workProp.setText(String.valueOf(qualityDetail.getString("area")) + "平方米");
            workTime.setText(String.valueOf(qualityDetail.getString("start")));
            manner.setText(String.valueOf(qualityDetail.getString("contact")));
            meanDepth.setText(String.valueOf(qualityDetail.getString("averDepth")) + "厘米");
            operArea.setText(String.valueOf(qualityDetail.getString("regionName")));
            complianceRate.setText(String.valueOf(qualityDetail.getString("valid")) + "%");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}

class ViewHolder {
    private final SparseArray<View> views;
    private View convertView;

    private ViewHolder(View convertView) {
        this.views = new SparseArray<View>();
        this.convertView = convertView;
        convertView.setTag(this);
    }

    public static ViewHolder get(View convertView) {
        if (convertView.getTag() == null) {
            return new ViewHolder(convertView);
        }
        return (ViewHolder) convertView.getTag();
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }
}
