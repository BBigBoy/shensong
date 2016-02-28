package com.fuyang.shensong.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by BigBigBoy on 2016/2/17.
 */
public class SpinnerArrayAdapter extends ArrayAdapter {
    public SpinnerArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SpinnerArrayAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SpinnerArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public SpinnerArrayAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SpinnerArrayAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public SpinnerArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ((TextView) view).setText(((TextView) view).getText().toString().split("-")[0]);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        ((TextView) view).setText(((TextView) view).getText().toString().split("-")[0]);
        return view;
    }
}
