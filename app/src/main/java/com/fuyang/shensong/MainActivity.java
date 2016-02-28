package com.fuyang.shensong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.SDKInitializer;
import com.fuyang.shensong.util.BaseActivity;

import me.fichardu.circleprogress.CircleProgress;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG_FRAG_LOGIN = "TAG_FRAG_LOGIN";
    MyApp app;
    FragmentManager fragmentManager;
    CircleProgress mProgressView;
    public View leftMainContainer, rightMainContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplication());
        setContentView(R.layout.activity_main);
        leftMainContainer = findViewById(R.id.activity_left_main_container);
        rightMainContainer = findViewById(R.id.activity_right_main_container);
        mProgressView = (CircleProgress) this.findViewById(R.id.progress);
        setTitle(R.string.app_name);
        fragmentManager = getSupportFragmentManager();
        app = (MyApp) getApplication();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String loginTag = "login";
        transaction.add(R.id.activity_main, new LoginFragment(), loginTag);
        //transaction.addToBackStack(loginTag);
        transaction.commit();
        findViewById(R.id.btn_quality_list).setOnClickListener(this);
        findViewById(R.id.btn_real_time).setOnClickListener(this);
    }

    public void initFragment() {
        replaceRightMainFragment(new QualityListFragment(), "QualityListFragment");
        replaceLeftMainFragment(new RealTimeFragment(), "RealTimeFragment");
        leftMainContainer.setVisibility(View.VISIBLE);
        rightMainContainer.setVisibility(View.GONE);
        findViewById(R.id.btn_real_time).performClick();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag("login") != null) {
            finish();
            return;
        }
        if (fragmentManager.findFragmentByTag("confirm") != null) {
            fragmentManager.beginTransaction().
                    remove(fragmentManager.findFragmentByTag("confirm"))
                    .setCustomAnimations(0, R.anim.slide_bottom_out).
                    commit();
            return;
        }
        if (rightMainContainer.getVisibility() == View.VISIBLE) {
            Fragment showingFragment = fragmentManager.findFragmentById(R.id.activity_right_main_container);
            if (showingFragment.getClass().getSimpleName()
                    .equals(QualityListFragment.class.getSimpleName())) {
                showConfirmFragment();
            } else {
                fragmentManager.beginTransaction().setCustomAnimations(0, R.anim.slide_right_out)
                        .remove(showingFragment).commit();
            }
            return;
        } else {
            Fragment showingFragment = fragmentManager.
                    findFragmentById(R.id.activity_left_main_container);
            if (showingFragment.getClass().getSimpleName()
                    .equals(RealTimeFragment.class.getSimpleName())) {
                showConfirmFragment();
            } else {
                fragmentManager.beginTransaction().setCustomAnimations(0, R.anim.slide_right_out)
                        .remove(showingFragment).commit();
            }
            return;
        }
    }

    private void showConfirmFragment() {
        ConfirmFragment confirmFragment = new ConfirmFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(0, 0, R.anim.slide_bottom_in, R.anim.slide_bottom_out);
        transaction.add(R.id.activity_main, confirmFragment, "confirm");
        //transaction.addToBackStack("confirm");
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.exitApp();
    }

    /**
     * 替换页面主内容的fragment
     *
     * @param fragment
     */
    public void replaceRightMainFragment(Fragment fragment) {
        replaceRightMainFragment(fragment, "rightMainFragment");
    }

    /**
     * 替换页面主内容的fragment
     *
     * @param fragment
     */
    public void replaceRightMainFragment(Fragment fragment, String tag) {
        replaceFragment(fragment, R.id.activity_right_main_container, tag);
    }

    /**
     * 替换页面主内容的fragment
     *
     * @param fragment
     */
    public void replaceLeftMainFragment(Fragment fragment) {
        replaceLeftMainFragment(fragment, "leftMainFragment");
    }

    /**
     * 替换页面主内容的fragment
     *
     * @param fragment
     */
    public void replaceLeftMainFragment(Fragment fragment, String tag) {
        replaceFragment(fragment, R.id.activity_left_main_container, tag);
    }

    public void replaceFragment(Fragment fragment, int containerId, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, tag);
        transaction.commit();
    }

    /**
     * 在页面主内容中添加一个fragment(右)
     *
     * @param fragment
     */
    public void addRightMainFragment(Fragment fragment, String tag) {
        addFragment(R.id.activity_right_main_container, fragment, tag);
    }

    /**
     * 在页面主内容中添加一个fragment（左）
     *
     * @param fragment
     */
    public void addLeftMainFragment(Fragment fragment, String tag) {
        addFragment(R.id.activity_left_main_container, fragment, tag);
    }

    /**
     * 在页面主内容中添加一个fragment
     *
     * @param fragment
     */
    public void addFragment(int containerId, Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        transaction.add(containerId, fragment, tag);
        //transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * 显示执行进度
     */
    public void showProgressAnim() {
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.startAnim();
    }

    /**
     * 停止执行进度显示
     */
    public void stopProgressAnim() {
        mProgressView.setVisibility(View.INVISIBLE);
        mProgressView.stopAnim();
    }

    /**
     * 显示主界面下方控制区域
     */
    public void showBottom() {
        findViewById(R.id.activity_main_bottom).setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏主界面下方控制区域
     */
    public void hideBottom() {
        findViewById(R.id.activity_main_bottom).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_real_time:
                findViewById(R.id.btn_real_time_img).setEnabled(true);
                findViewById(R.id.btn_real_time_txt).setEnabled(true);
                findViewById(R.id.btn_quality_list_img).setEnabled(false);
                findViewById(R.id.btn_quality_list_txt).setEnabled(false);
                leftMainContainer.setVisibility(View.VISIBLE);
                rightMainContainer.setVisibility(View.GONE);
                break;
            case R.id.btn_quality_list:
                findViewById(R.id.btn_real_time_img).setEnabled(false);
                findViewById(R.id.btn_real_time_txt).setEnabled(false);
                findViewById(R.id.btn_quality_list_img).setEnabled(true);
                findViewById(R.id.btn_quality_list_txt).setEnabled(true);
                leftMainContainer.setVisibility(View.GONE);
                rightMainContainer.setVisibility(View.VISIBLE);
                break;
        }
    }
}
