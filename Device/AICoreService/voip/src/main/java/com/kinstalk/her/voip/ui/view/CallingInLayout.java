package com.kinstalk.her.voip.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.model.entity.ContactEntity;

/**
 * Created by siqing on 17/5/18.
 */

public class CallingInLayout extends LinearLayout implements View.OnClickListener {
    private Button acceptBtn, refuseBtn;
    private ImageView headImg;
    private TextView nameText, descText;
    private ContactEntity mContactEntity;
    private CallingOptionListener optionListener;

    public interface CallingOptionListener {
        void onAcceptClick(View v, ContactEntity entity);

        void onRefuseClick(View v, ContactEntity entity);
    }

    public CallingInLayout(Context context) {
        super(context);
    }

    public CallingInLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        acceptBtn = (Button) findViewById(R.id.accept_btn);
        refuseBtn = (Button) findViewById(R.id.refuse_btn);
        headImg = (ImageView) findViewById(R.id.head_img);
        nameText = (TextView) findViewById(R.id.name_text);
        descText = (TextView) findViewById(R.id.desc_text);
        acceptBtn.setOnClickListener(this);
        refuseBtn.setOnClickListener(this);
    }

    /**
     * 设置呼入状态描述（语音或者视频）
     *
     * @param desc
     */
    public void setDescText(String desc) {
        if (descText != null) {
            descText.setText(desc);
        }
    }

    /**
     * 刷新呼入用户信息
     *
     * @param contactEntity
     */
    public void notifyCallingInUserInfo(ContactEntity contactEntity) {
        this.mContactEntity = contactEntity;
        if (contactEntity != null) {
            Glide.with(getContext()).load(contactEntity.getIcon()).into(headImg);
            nameText.setText(contactEntity.getName());
        }
    }

    @Override
    public void onClick(View view) {
        if (optionListener == null) {
            return;
        }
        if (view.getId() == R.id.accept_btn) {
            optionListener.onAcceptClick(view, mContactEntity);
        } else if (view.getId() == R.id.refuse_btn) {
            optionListener.onRefuseClick(view, mContactEntity);
        }
    }

    public void setOptionListener(CallingOptionListener optionListener) {
        this.optionListener = optionListener;
    }
}
