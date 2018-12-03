package com.kinstalk.her.voip.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.kinstalk.her.voip.ui.fragment.UserInfoFragmentStyle2;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.kinstalk.her.voip.utils.StyleUtils;

/**
 * Created by siqing on 17/5/22.
 * 用户信息页面（已接收/未接受）
 */

public class UserInfoActivity extends ContactBaseActivity {

    public static final String INTENT_CONTACTENTITY = "contact_entity";

    private ContactEntity mEntity;

    /**
     * 启动用户信息页面
     *
     * @param context
     */
    public static void actionStart(Context context, ContactEntity entity) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(INTENT_CONTACTENTITY, entity);
        context.startActivity(intent);
    }

    @Override
    protected void initBaseDatas() {
        super.initBaseDatas();
        mEntity = (ContactEntity) getIntent().getSerializableExtra(INTENT_CONTACTENTITY);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_user_info);
    }

    @Override
    protected void initActions() {
    }

    @Override
    protected void initDatas() {
        if (mEntity == null) {
            LogUtils.e("用户信息为空");
            finish();
            return;
        }
        refreshUI();
    }


    /**
     * 刷新页面
     */
    private void refreshUI() {
        Fragment fragment;
        fragment = UserInfoFragmentStyle2.newInstance(mEntity);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, fragment).commitAllowingStateLoss();
    }
}
