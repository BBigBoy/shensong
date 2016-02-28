package com.fuyang.shensong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 基础的fragment。必须包含title（布局中包含top_title文件）
 */
public class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        View actionBack = view.findViewById(R.id.action_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * 设置标题栏文字
     *
     * @param title
     */
    public void setActionTitle(String title) {
        if (getView() != null) {
            TextView actionTitle = ((TextView) getView().findViewById(R.id.action_title));
            actionTitle.setText(title);
        }
    }

    /**
     * 显示或隐藏返回按钮
     *
     */
    public void showActionBack(boolean display) {
        if (getView() != null) {
            View actionBack = getView().findViewById(R.id.action_back);
            if (display) {
                actionBack.setVisibility(View.VISIBLE);
            } else {
                actionBack.setVisibility(View.INVISIBLE);
            }
        }
    }
    /**
     * 显示或隐藏筛选按钮
     *
     */
    public void showActionFilter(boolean display) {
        if (getView() != null) {
            View actionFilter = getView().findViewById(R.id.action_filter);
            if (display) {
                actionFilter.setVisibility(View.VISIBLE);
            } else {
                actionFilter.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置筛选按钮的点击事件
     *
     * @param listener
     */
    public void setActionFilterClickListener(View.OnClickListener listener) {
        if (getView() != null) {
            View actionFilter = getView().findViewById(R.id.action_filter);
            actionFilter.setOnClickListener(listener);
        }
    }
    /**
     * 设置返回按钮的点击事件
     *
     * @param listener
     */
    public void setActionBackClickListener(View.OnClickListener listener) {
        if (getView() != null) {
            View actionBack = getView().findViewById(R.id.action_back);
            actionBack.setOnClickListener(listener);
        }
    }

    protected void displayToast(final String toastContent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_LONG).show();
            }
        });
    }
}
