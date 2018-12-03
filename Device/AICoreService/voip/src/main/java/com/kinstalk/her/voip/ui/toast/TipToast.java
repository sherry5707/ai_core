package com.kinstalk.her.voip.ui.toast;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinstalk.her.voip.R;


public class TipToast extends Toast {
    private static final int MAX_LENGHT = 30;
    private static TipToast tipsToast = null;
    private Context mContext;
    public static final boolean isToastAble = false;

    public TipToast(Context context) {
        super(context);
        this.mContext = context;
    }

    public static TipToast getInstance(Context context) {
        if (null == tipsToast) {
            synchronized (TipToast.class) {
                if (null == tipsToast) {
                    tipsToast = new TipToast(context.getApplicationContext());
                }
            }
        }
        return tipsToast;
    }

    public void showView(int imageId, String text) {
        showJYToastView(imageId, text, Toast.LENGTH_SHORT);
    }

    public void showJYToastView(int imageId, String text, int duration) {
        if (!isToastAble) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_toast, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.tips_toast_iv);
        TextView textView = (TextView) view.findViewById(R.id.tips_toast_tv);

        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }

        if (imageId > 0) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(imageId);
        } else {
            imageView.setVisibility(View.GONE);
        }

        tipsToast.setView(view);
        tipsToast.setGravity(Gravity.CENTER, 0, 0);
        tipsToast.setDuration(duration);

        tipsToast.show();
    }
}
