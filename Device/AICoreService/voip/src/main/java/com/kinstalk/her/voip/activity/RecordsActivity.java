package com.kinstalk.her.voip.activity;

import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.ui.fragment.CallRecordsFragmentStyle2;


public class RecordsActivity extends ContactBaseActivity {
    public static final String INTNET_ISAISTART = "isAiStart";
    public boolean isAIStart = false;
    private CallRecordsFragmentStyle2 fragment;

    public static void actionStart(Context context) {
        actionStart(context, false);
    }

    public static void actionStart(Context context, boolean isAIStart) {
        Intent intent = new Intent(context, RecordsActivity.class);
        intent.putExtra(RecordsActivity.INTNET_ISAISTART, isAIStart);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void initBaseDatas() {
        super.initBaseDatas();
        isAIStart = getIntent().getBooleanExtra(INTNET_ISAISTART, false);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_container);
        fragment = CallRecordsFragmentStyle2.newInstance(isAIStart);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void initActions() {

    }

    @Override
    protected void initDatas() {
        if (fragment != null) {
            fragment.changePageStartedStatus(isAIStart);
        }
    }
}
