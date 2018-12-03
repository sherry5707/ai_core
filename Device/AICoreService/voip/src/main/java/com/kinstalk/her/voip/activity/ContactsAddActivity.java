package com.kinstalk.her.voip.activity;

import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.voip.R;

/**
 * Created by siqing on 17/5/12.
 */

public class ContactsAddActivity extends ContactBaseActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ContactsAddActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void initViews() {
        setContentView(R.layout.activity_contacts_add);
    }

    @Override
    protected void initActions() {

    }

    @Override
    protected void initDatas() {

    }
}
