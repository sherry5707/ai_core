package com.kinstalk.her.voip.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.activity.dispatch.ActivityDispatch;
import com.kinstalk.her.voip.manager.PrivacyManager;
import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.kinstalk.her.voip.ui.utils.StatisticsUtils;
import com.kinstalk.her.voip.utils.QVoipBroadcastSender;
import com.kinstalk.her.voip.utils.Utils;
import com.tencent.xiaowei.util.QLog;

import static com.kinstalk.her.voip.activity.UserInfoActivity.INTENT_CONTACTENTITY;


public class UserInfoFragmentStyle2 extends BaseFragment implements View.OnClickListener{
    public static final String TAG = "UserInfoFragmentStyle2";
    private ImageView headImg;
    private TextView nameText, statusText, nameFirstText;
    private Button audioBtn, videoBtn;
    private ImageButton backBtn;

    private ContactEntity mEntity;
    private boolean isVoipCalling = false;
    private PrivacyManager privacyManager;

    private BroadcastReceiver voipReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String callType = intent.getStringExtra(QVoipBroadcastSender.INTENT_TYPE);
            if (QVoipBroadcastSender.TYPE_CALLING.equals(callType)) {
                LogUtils.i("UserInfoFragmentStyle2 ： voip来电");
                isVoipCalling = true;
            } else {
                LogUtils.i("UserInfoFragmentStyle2 ： voip挂断");
                isVoipCalling = false;
            }
        }
    };

    public static UserInfoFragmentStyle2 newInstance(ContactEntity entity){
        UserInfoFragmentStyle2 fragment = new UserInfoFragmentStyle2();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_CONTACTENTITY, entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        QLog.d(TAG,"onCreateView init privacyManager");
        privacyManager = PrivacyManager.getInstance(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected void initBaseDatas(Bundle savedInstanceState) {
        super.initBaseDatas(savedInstanceState);
        mEntity = (ContactEntity) getArguments().getSerializable(INTENT_CONTACTENTITY);
        IntentFilter intentFilter = new IntentFilter(QVoipBroadcastSender.ACTION_KINSTALK_VOIP);
        getActivity().registerReceiver(voipReceiver, intentFilter);
    }

    @Override
    protected int getRootLayoutRes() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        headImg = (ImageView) contentView.findViewById(R.id.head_img);
        nameText = (TextView) contentView.findViewById(R.id.name_text);
        nameFirstText = (TextView) contentView.findViewById(R.id.name_first_text);
        statusText = (TextView) contentView.findViewById(R.id.status_text);
        audioBtn = (Button) contentView.findViewById(R.id.audio_btn);
        videoBtn = (Button) contentView.findViewById(R.id.video_btn);
        backBtn = (ImageButton) contentView.findViewById(R.id.back_btn);

    }

    @Override
    protected void initActions(View contentView) {
        audioBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    protected void initDatas(View contentView) {
        if (mEntity == null) {
            LogUtils.e("用户信息为空");
            getActivity().finish();
            return;
        }
        refreshUI();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(voipReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.audio_btn) {
            StatisticsUtils.touchCallingOut();
            ActivityDispatch.callAudio(getActivity(), mEntity);

        } else if (i == R.id.video_btn) {
            if (isVoipCalling && Utils.isProessRunning(getActivity(), getActivity().getPackageName() + ":audio")) {
                Toast.makeText(getActivity(), "通话中，请结束通话后再试", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isPrivacy = privacyManager.isPrivacy();
            QLog.d("UserInfoFragmentStyle2","isPrivacy =="+isPrivacy);
            if (isPrivacy) {
                PrivacyManager.getInstance(getActivity()).showVoipPrivacyDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PrivacyManager.getInstance(getActivity()).closePrivacy();
                        StatisticsUtils.touchCallingOut();
                        ActivityDispatch.callVideo(getActivity(), mEntity);
                    }
                });
                return;
            } else {
                StatisticsUtils.touchCallingOut();
                LogUtils.d("QUI-KPI---点击拨打视频通话");
                ActivityDispatch.callVideo(getActivity(), mEntity);
            }
        } else if (i == R.id.back_btn) {
            getActivity().finish();
        }
   //     getActivity().finish();
    }

    /**
     * 刷新页面
     */
    private void refreshUI() {
        statusText.setVisibility(View.GONE);
        nameText.setText(mEntity.getName());
        if (TextUtils.isEmpty(mEntity.getIcon()) && !TextUtils.isEmpty(mEntity.getName())) {
            nameFirstText.setVisibility(View.VISIBLE);
            nameFirstText.setText(mEntity.getName().substring(0, 1));
        } else {
            nameFirstText.setVisibility(View.GONE);
        }
        Glide.with(this)
                .load(mEntity.getIcon())
                .placeholder(new ColorDrawable(Color.WHITE))
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        headImg.setImageDrawable(resource);
                    }
                });
    }
}
