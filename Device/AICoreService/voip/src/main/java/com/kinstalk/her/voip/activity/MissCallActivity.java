package com.kinstalk.her.voip.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.activity.dispatch.ActivityDispatch;
import com.kinstalk.her.voip.model.db.RecordDbService;
import com.kinstalk.her.voip.model.entity.RecordEntity;
import com.kinstalk.her.voip.ui.utils.StatisticsUtils;
import com.kinstalk.her.voip.utils.DateUtils;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
import com.tencent.xiaowei.info.XWContactInfo;


import java.util.ArrayList;
import java.util.List;


public class MissCallActivity extends ContactBaseActivity {

    private ImageView headImg, videoCallImg;
    private TextView nameText, timeText, missCallTitleText;
    private RecyclerView missCallMemberRv;
    private Button ignoreBtn;

    private RecordDbService recordDbService;
    private MissCallAdapter mAdapter;
    public List<RecordEntity> missCallRecords;

    private List<RecordEntity> moreMissCallRecords = new ArrayList<>();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MissCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordDbService = new RecordDbService(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_miss_call);
        headImg = (ImageView) findViewById(R.id.head_img);
        nameText = (TextView) findViewById(R.id.name_text);
        timeText = (TextView) findViewById(R.id.time_text);
        videoCallImg = (ImageView) findViewById(R.id.video_img);
        missCallTitleText = (TextView) findViewById(R.id.miss_call_title_text);
        missCallMemberRv = (RecyclerView) findViewById(R.id.miss_call_member_rv);
        ignoreBtn = (Button) findViewById(R.id.ignore_btn);
        mAdapter = new MissCallAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        missCallMemberRv.setLayoutManager(linearLayoutManager);
        missCallMemberRv.setAdapter(mAdapter);
        cancelExitAuto();
    }

    @Override
    protected void initActions() {
        ignoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        recordDbService.signRead(missCallRecords);
                    }
                }.start();
                finish();
            }
        });
        videoCallImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPrivacy = ActivityDispatch.callVideoWithPrivacyInterupt(MissCallActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StatisticsUtils.touchCallingOut();
                        ActivityDispatch.callVideo(MissCallActivity.this, Long.parseLong(getFirstEntity().getPeerUid()));
                        finish();
                    }
                });
                if (isPrivacy) {
                    return;
                }
                ActivityDispatch.callVideo(MissCallActivity.this, Long.parseLong(getFirstEntity().getPeerUid()));
                finish();
            }
        });
    }

    @Override
    protected void initDatas() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        recordDbService.signRead(missCallRecords);
        recordDbService.destory();
        super.onDestroy();
    }

    private void loadData(){
        new Thread() {
            @Override
            public void run() {
                missCallRecords = recordDbService.queryUnReadRecords();
                if (missCallRecords.isEmpty()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyCallRecordsDataChange();
                    }
                });

            }
        }.start();
    }

    /**
     * 控制更多未接电话
     *
     * @param visibile
     */
    private void setMoreVisibile(int visibile) {
        missCallTitleText.setVisibility(visibile);
        missCallMemberRv.setVisibility(visibile);
    }

    /**
     * 更新最新来电的人的UI
     */
    private void refreshFirstMember() {
        RecordEntity entity = getFirstEntity();
        XWContactInfo info = XWDeviceBaseManager.getXWContactInfo(entity.getPeerUid());
        Glide.with(this).load(info.headUrl).asBitmap().into(headImg);
        nameText.setText(entity.getPeerName());
        timeText.setText(DateUtils.parseRecordDate(entity.getCreateTime()) + "未接来电");
    }

    /**
     * 更新更多来电UI
     */
    private void refreshMoreMember() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 数据查询后更新UI
     */
    private void notifyCallRecordsDataChange() {
        refreshFirstMember();
        moreMissCallRecords.clear();
        List<RecordEntity> moreRecordList = dealMoreMissCall();
        if(moreRecordList.isEmpty()) {
            setMoreVisibile(View.INVISIBLE);
        } else {
            setMoreVisibile(View.VISIBLE);
            moreMissCallRecords.addAll(moreRecordList);
            refreshMoreMember();
        }
    }

    private List<RecordEntity> dealMoreMissCall() {
        List<RecordEntity> moreMissList = new ArrayList<>();
        for (RecordEntity entity : missCallRecords) {
            if (moreMissList.size() == 5) {//如果已经有五条数据了
                break;
            }
            boolean isContain = false;
            if (entity.getPeerUid().equals(getFirstEntity().getPeerUid())) {
                continue;
            }
            for (RecordEntity moreEntity : moreMissList) {
                //判断是否跟第一条数据重复或者已经在moreMissCall里面了（如果重复不加到missCall）
                if (moreEntity.getPeerUid().equals(entity.getPeerUid())) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                moreMissList.add(entity);
            }
        }
        if (moreMissList.isEmpty()) {
            return moreMissList;
        }
        RecordEntity moreEntity = new RecordEntity();
        moreEntity.setViewType(1);
        moreMissList.add(moreEntity);
        return moreMissList;
    }

    /**
     * 获取第一个人的信息
     *
     * @return
     */
    private RecordEntity getFirstEntity() {
        return missCallRecords.get(0);
    }

    class MissCallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if (viewType == 0) {
                holder = new MemberHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_miss_call_more_member, null));
            } else {
                holder = new MoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_miss_call_more_member_more, null));
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MemberHolder) {
                ((MemberHolder) holder).refreshUI(moreMissCallRecords.get(position));
            } else if (holder instanceof MoreHolder) {
                ((MoreHolder) holder).refreshUI(moreMissCallRecords.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return moreMissCallRecords.size();
        }

        @Override
        public int getItemViewType(int position) {
            return moreMissCallRecords.get(position).getViewType();
        }
    }

    private class MemberHolder extends RecyclerView.ViewHolder {
        private ImageView headImg;

        public MemberHolder(View itemView) {
            super(itemView);
            headImg = (ImageView) itemView.findViewById(R.id.head_img);
        }

        public void refreshUI(RecordEntity entity) {
            XWContactInfo info = XWDeviceBaseManager.getXWContactInfo(entity.getPeerUid());
            Glide.with(MissCallActivity.this).load(info.headUrl).asBitmap().into(headImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordsActivity.actionStart(MissCallActivity.this);
                }
            });
        }
    }

    private class MoreHolder extends RecyclerView.ViewHolder {
        private ImageView moreBtn;

        public MoreHolder(View itemView) {
            super(itemView);
            moreBtn = (ImageView) itemView.findViewById(R.id.more_img);
        }

        public void refreshUI(RecordEntity entity) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordsActivity.actionStart(MissCallActivity.this);
                }
            });
        }
    }
}
