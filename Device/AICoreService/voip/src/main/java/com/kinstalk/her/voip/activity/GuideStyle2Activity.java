package com.kinstalk.her.voip.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.utils.GuideSharePreHelper;

/**
 * Created by siqing on 17/8/30.
 */

public class GuideStyle2Activity extends ContactBaseActivity {
    private TextView text2TextView;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GuideStyle2Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_guide_style2);
//        text2TextView = (TextView) findViewById(R.id.text2_text);
//        text2TextView.setText(Html.fromHtml(getString(R.string.voip_guide_desc_html_2)));
//        ((TextView) findViewById(R.id.text3_text)).setText(Html.fromHtml(getString(R.string.voip_guide_desc_html_3)));
//        text2TextView.setText(Html.fromHtml("2.打开云小微APP，点击<font color=\"red\">“我的-添加小微设备”，</font>扫描亲见设备上的二维码进行绑定；"));
    }

    @Override
    protected void initActions() {
    }

    @Override
    protected void initDatas() {
        GuideSharePreHelper.setGuideShowed(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onBackClick(View v) {
        finish();
    }

}
