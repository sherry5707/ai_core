package com.kinstalk.her.voip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.kinstalk.her.voip.ui.utils.LogUtils;

import java.lang.reflect.Method;

import ly.count.android.sdk.Countly;

/**
 * Created by siqing on 17/5/12.
 */

public abstract class BaseActivity extends FragmentActivity {
    private String TAG = "BaseActivity";
    private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.e(TAG, "onCreate");
        LogUtils.d("QUI-KPI---onCreate : " + getClass().getSimpleName());
        hideNavigationBar();
        initBaseDatas();
        initViews();
        initActions();
        initDatas();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");
        setIntent(intent);
        initBaseDatas();
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("QUI-KPI---onResume : " + getClass().getSimpleName());
        hideNavigationBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Countly.sharedInstance().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Countly.sharedInstance().onStop();
    }


    private void hideNavigationBar() {
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public void replaceFragment(Fragment fragment, String tag, int contentId) {
        getSupportFragmentManager().beginTransaction().replace(contentId, fragment, tag)
                .commitAllowingStateLoss();
    }

    public void removeFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    public Fragment findFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * 取消自动回到首页
     */
    protected void cancelExitAuto(){
        LogUtils.e("取消自动回到首页");
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", new Class[]{boolean.class});
            method.setAccessible(true);
            Object object = method.invoke(attr, false);
        } catch (NoSuchMethodException e) {
            LogUtils.e("取消自动回到首页:NoSuchMethodException");
            e.printStackTrace();
        }catch (Exception e1){
            LogUtils.e("取消自动回到首页: e1 : " + e1.toString());
            e1.printStackTrace();
        }
        getWindow().setAttributes(attr);
    }

    protected void initBaseDatas() {
        Log.e(TAG, "initBaseDatas");
    }

    protected abstract void initViews();

    protected abstract void initActions();

    protected abstract void initDatas();
}
