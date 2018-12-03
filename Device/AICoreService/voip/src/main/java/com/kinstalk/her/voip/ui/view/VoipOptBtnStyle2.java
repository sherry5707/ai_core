package com.kinstalk.her.voip.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinstalk.her.voip.R;

/**
 * Created by siqing on 17/6/6.
 */

public class VoipOptBtnStyle2 extends LinearLayout {
    private ImageView optBtn;
    private TextView optText;
    private int mResource;
    private String mOpt;

    public VoipOptBtnStyle2(Context context) {
        this(context, null);
    }

    public VoipOptBtnStyle2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_opt_btn_style2, this, true);
        optBtn = (ImageView) findViewById(R.id.opt_btn);
        optText = (TextView) findViewById(R.id.opt_text);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setOptResource(int resource) {
        this.mResource = resource;
        if (optBtn != null) {
            optBtn.setBackgroundResource(resource);
        }
    }

    public void setOptText(String opt) {
        this.mOpt = opt;
        if (optText != null) {
            optText.setText(opt);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        optBtn.setSelected(selected);
    }

    @Override
    public boolean isSelected() {
        return optBtn.isSelected();
    }
}
