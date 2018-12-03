package com.kinstalk.her.voip.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.model.entity.ContactEntity;

/**
 * Created by siqing on 17/5/19.
 * 视频通话中
 */

public class CallingVideoLayout extends FrameLayout implements View.OnClickListener {
    private TextView nameText, timeText;
    private Button silenceBtn, hangUpBtn, cameraBtn;

    private OnClickListener optionListener;

    public CallingVideoLayout(@NonNull Context context) {
        super(context);
    }

    public CallingVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        nameText = (TextView) findViewById(R.id.calling_name_text);
        timeText = (TextView) findViewById(R.id.calling_time_text);
        silenceBtn = (Button) findViewById(R.id.calling_silence_btn);
        hangUpBtn = (Button) findViewById(R.id.calling_hangup_btn);
        cameraBtn = (Button) findViewById(R.id.calling_camera_btn);
        silenceBtn.setOnClickListener(this);
        hangUpBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (optionListener != null) {
            optionListener.onClick(v);
        }
    }

    /**
     * 更新名字，开始计时
     */
    public void notifyUIRefresh(ContactEntity entity) {
        if (entity == null) {
            return;
        }
        if (nameText != null) {
            nameText.setText(entity.getName());
        }
    }

    public void setOptionListener(OnClickListener optionListener) {
        this.optionListener = optionListener;
    }
}
