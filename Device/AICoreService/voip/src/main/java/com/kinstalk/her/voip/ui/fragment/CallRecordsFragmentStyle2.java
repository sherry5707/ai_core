package com.kinstalk.her.voip.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.activity.MainActivity;
import com.kinstalk.her.voip.activity.RecordsActivity;
import com.kinstalk.her.voip.activity.UserInfoActivity;
import com.kinstalk.her.voip.model.db.RecordConstant;
import com.kinstalk.her.voip.model.db.RecordDbService;
import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.kinstalk.her.voip.model.entity.RecordEntity;
import com.kinstalk.her.voip.ui.fragment.decoration.RecordDecoration;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.kinstalk.her.voip.utils.DateUtils;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
import com.tencent.xiaowei.info.XWContactInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallRecordsFragmentStyle2 extends BaseFragment {
    private RecyclerView recordRecycler;
    private ImageButton backBtn;
    private TextView contactsBtn;
    private TextView emptyText;
    private RecordsAdapter mAdapter;
    private RecordDbService recordService;
    private List<RecordEntity> recordList = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isAIStart = false;
    public static final String BUNDLE_ISAI = "isAIStart";

    public static CallRecordsFragmentStyle2 newInstance(boolean isAIStart) {
        CallRecordsFragmentStyle2 fragment = new CallRecordsFragmentStyle2();
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_ISAI, isAIStart);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecordsActivity) {
            isAIStart = ((RecordsActivity) context).isAIStart;
        }
    }

    @Override
    protected void initBaseDatas(Bundle savedInstanceState) {
        super.initBaseDatas(savedInstanceState);
        recordService = new RecordDbService(getContext());
        isAIStart = getArguments() != null ?  getArguments().getBoolean(BUNDLE_ISAI) : false;
    }

    @Override
    protected int getRootLayoutRes() {
        return R.layout.fragment_call_records_style2;
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        backBtn = (ImageButton) contentView.findViewById(R.id.back_btn);
        contactsBtn = (TextView) contentView.findViewById(R.id.contacts_btn);
        recordRecycler = (RecyclerView) contentView.findViewById(R.id.record_recycler);
        emptyText = (TextView) contentView.findViewById(R.id.empty_text);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mAdapter = new RecordsAdapter();
        recordRecycler.setLayoutManager(mLayoutManager);
        recordRecycler.setAdapter(mAdapter);
        recordRecycler.addItemDecoration(new RecordDecoration(getContext(), RecordDecoration.VERTICAL_LIST));
        ((SimpleItemAnimator)recordRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        changePageStartedStatus(isAIStart);
    }

    @Override
    protected void initActions(View contentView) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isAIStart) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                    getActivity().finish();
//                } else {
//                    getActivity().finish();
//                }
            }
        });
        contactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     MainActivity.actionStart(getActivity());
                getActivity().finish();
            }
        });
        recordRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void initDatas(View contentView) {
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroy() {
        recordService.destory();
        super.onDestroy();
    }

    public void changePageStartedStatus(boolean isAIStart){
        this.isAIStart = isAIStart;
        if (contactsBtn != null && backBtn != null) {
//            if (isAIStart) {
                backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.magellan_icon_home));
                contactsBtn.setVisibility(View.VISIBLE);
//            } else {
//                backBtn.setImageDrawable(getResources().getDrawable(R.mipmap.magellan_user_back));
//                contactsBtn.setVisibility(View.GONE);
//            }
        }
    }

    /**
     * 更新数据
     */
    private void refreshData() {
        new Thread() {
            @Override
            public void run() {
                recordList.clear();
                long startTime = System.currentTimeMillis();
                List<RecordEntity> records = recordService.queryRecordsWithMissCallMerge();
                for (RecordEntity entity: records) {
                    XWContactInfo info = XWDeviceBaseManager.getXWContactInfo(entity.getPeerUid());
                    if (!TextUtils.isEmpty(info.remark)) {
                        entity.setPeerName(info.remark);
                    }
                }
                LogUtils.e("查询耗时 : " + (System.currentTimeMillis() - startTime));
                recordList.addAll(records);
                refreshUI();
            }
        }.start();
    }

    private void refreshUI() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recordList.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void deleteData(int position) {
        SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
        if (null != viewCache) {
            viewCache.quickClose();
        }
        recordService.deleteRecords(recordList.get(position));
//        recordService.deleteRecord(recordList.get(position).getId());
        recordList.remove(position);

        mAdapter.notifyItemRemoved(position);
        if(position != recordList.size()) {      // 这个判断的意义就是如果移除的是最后一个，就不用管它了，= =whatever，老板还不发工资啊啊啊啊啊啊
            mAdapter.notifyItemRangeChanged(position, recordList.size() - position);
        }
//        mAdapter.notifyDataSetChanged();
        if (recordList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    public void notifyDataChange(){

    }


    class RecordsAdapter extends RecyclerView.Adapter<RecordsHolder> {

        @Override
        public RecordsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_records_callin_style2, null);
            return new RecordsHolder(item);
        }

        @Override
        public void onBindViewHolder(RecordsHolder holder, int position) {
            holder.refreshUI(recordList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return recordList.size();
        }
    }

    final class RecordsHolder extends RecyclerView.ViewHolder {
        private ImageView callIcon;
        private TextView nameText;
        private TextView timeText;
        private TextView numberText;
        private Button deleteBtn;
        private View contentBox;

        public RecordsHolder(View itemView) {
            super(itemView);
            callIcon = (ImageView) itemView.findViewById(R.id.call_icon);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
            numberText = (TextView) itemView.findViewById(R.id.number_text);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
            contentBox = itemView.findViewById(R.id.content_layout);
        }

        public void refreshUI(final RecordEntity recordEntity, final int position) {
            if (recordEntity.getCallType() == RecordConstant.CALL_TYPE_OUT) {
                callIcon.setVisibility(View.VISIBLE);
            } else {
                callIcon.setVisibility(View.INVISIBLE);
            }
            if (recordEntity.getCallType() == RecordConstant.CALL_TYPE_IN && recordEntity.isAccept() != RecordConstant.ACCEPT) {
                nameText.setSelected(true);
                nameText.setText(recordEntity.getPeerName());
                numberText.setSelected(true);
                numberText.setText(String.valueOf(recordEntity.getMergeRecords().size()));
                numberText.setVisibility(View.VISIBLE);
            } else {
                nameText.setSelected(false);
                numberText.setSelected(false);
                nameText.setText(recordEntity.getPeerName());
                numberText.setVisibility(View.GONE);
            }
            timeText.setText(parseDate(recordEntity.getCreateTime()));
            contentBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactEntity entity = new ContactEntity();
                    entity.setUin(Long.parseLong(recordEntity.getPeerUid()));
                    XWContactInfo infoEntity = XWDeviceBaseManager.getXWContactInfo(recordEntity.getPeerUid());
                    entity.setName(infoEntity.remark);
                    entity.setIcon(infoEntity.headUrl);
                    UserInfoActivity.actionStart(getActivity(), entity);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteData(position);
                }
            });
        }

        private String parseDate(long time) {
            Date date = new Date(time);
            if (DateUtils.isToday(date)) {
                return DateUtils.getTodayDateStr(date);
            } else if (DateUtils.isWeekIn(date)) {
                return DateUtils.getWeekStr(time);
            } else {
                return DateUtils.getDateStr(date);
            }
        }

    }
}
