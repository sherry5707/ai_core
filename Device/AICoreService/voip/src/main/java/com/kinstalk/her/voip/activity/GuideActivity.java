package com.kinstalk.her.voip.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.utils.GuideSharePreHelper;

/**
 * Created by siqing on 17/8/30.
 */

public class GuideActivity extends ContactBaseActivity {

    private ImageView blueImg, greenImg;
    private View guideBox;
    private Button confirmBtn;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_guide);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        guideBox = findViewById(R.id.guide_layout);
        blueImg = (ImageView) findViewById(R.id.blue_img);
        greenImg = (ImageView) findViewById(R.id.green_img);
    }

    @Override
    protected void initActions() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClick();
            }
        });
    }

    @Override
    protected void initDatas() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        startGuideAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopGuideAnimation();
    }

    private void startGuideAnimation() {
        stopGuideAnimation();
        Animation animationBlue = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationBlue.setRepeatCount(Animation.INFINITE);
        animationBlue.setDuration(4000);
        Animation animationGreen = new RotateAnimation(0, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationGreen.setRepeatCount(Animation.INFINITE);
        animationGreen.setDuration(4000);
        blueImg.startAnimation(animationBlue);
        greenImg.startAnimation(animationGreen);
    }

    private void stopGuideAnimation(){
        blueImg.clearAnimation();
        greenImg.clearAnimation();
    }

    private void confirmClick() {
        GuideSharePreHelper.setGuideShowed(this);
        blueImg.clearAnimation();
        greenImg.clearAnimation();
        finish();
    }

    @Override
    protected void onDestroy() {
        blueImg.setImageDrawable(null);
        greenImg.setImageDrawable(null);
        super.onDestroy();
    }
}
