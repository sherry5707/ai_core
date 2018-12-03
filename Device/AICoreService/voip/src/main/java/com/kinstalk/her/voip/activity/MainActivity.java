package com.kinstalk.her.voip.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.ui.fragment.MainFragmentStyle2;
import com.kinstalk.her.voip.utils.StyleUtils;

public class MainActivity extends ContactBaseActivity {


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);
        inflatePage();
    }

    @Override
    protected void initActions() {
    }

    @Override
    protected void initDatas() {
    //    Intent i = new Intent();
   //     i.setComponent(new ComponentName("kinstalk.com.qloveaicore", "kinstalk.com.qloveaicore.QAICoreService"));
    //    startService(i);
    }

    private void inflatePage(){
        Fragment fragment;
        fragment = MainFragmentStyle2.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, fragment).commitAllowingStateLoss();
    }

}
