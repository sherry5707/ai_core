package com.kinstalk.her.voip.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kinstalk.her.voip.ui.utils.VoipUtils;
import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.activity.GuideStyle2Activity;
import com.kinstalk.her.voip.activity.RecordsActivity;
import com.kinstalk.her.voip.activity.dispatch.ActivityDispatch;
import com.kinstalk.her.voip.model.db.RecordDbService;
import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.kinstalk.her.voip.recevier.NotifyReceiver;
import com.kinstalk.her.voip.ui.utils.LogUtils;
//import com.tencent.aiaudio.TXSdkWrapper;
import com.tencent.xiaowei.info.XWBinderRemark;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
import com.tencent.xiaowei.info.XWBinderInfo;
//import com.tencent.device.TXDeviceSDK;
//import com.tencent.device.info.TXAIAudioFriendInfo;
import com.tencent.xiaowei.info.XWContactInfo;


import java.util.ArrayList;
import java.util.List;

//import static com.tencent.aiaudio.CommonDef.ACTION_ON_ERASE_ALL_BINDERS;
//import static com.tencent.aiaudio.CommonDef.ACTION_ON_RECEIVE_REQUEST_ADD_FRIEND;


public class MainFragmentStyle2 extends BaseFragment implements NotifyReceiver.NotifyDelegate {
    private RecyclerView contactsRv;
    private TextView recordsBtn;
    private TextView guideText;
    private ContactsAdapter mAdapter;

    private NotifyReceiver notifyReceiver;
    private RecordDbService recordDbService;

    private List<ContactEntity> contactsList = new ArrayList<>();

    private boolean isLogining = true;

    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (VoipUtils.ACTION_LOGIN_SUCCESS.equals(intent.getAction())) {
                isLogining = true;
                LogUtils.e("登录成功 : " + isLogining);
                if (mAdapter != null) {
                    refreshBinderUserList();
                }
            } else {
                isLogining = false;
                LogUtils.e("未登录 : " + isLogining);
            }
        }
    };

    public static MainFragmentStyle2 newInstance() {
        MainFragmentStyle2 fragment = new MainFragmentStyle2();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        recordDbService = new RecordDbService(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initBaseDatas(Bundle savedInstanceState) {
        super.initBaseDatas(savedInstanceState);
        notifyReceiver = new NotifyReceiver();
        notifyReceiver.setNotifyDelegate(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(VoipUtils.ACTION_ON_BINDER_LIST_CHANGE);
//        filter.addAction(ACTION_ON_ERASE_ALL_BINDERS);
//        filter.addAction(ACTION_ON_RECEIVE_REQUEST_ADD_FRIEND);
//        filter.addAction(CommonDef.ACTION_ON_RECEIVE_MSG);
        getContext().registerReceiver(notifyReceiver, filter);
    }

    @Override
    public void onDestroy() {
        recordDbService.destory();
        if (notifyReceiver != null) {
            getContext().unregisterReceiver(notifyReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected int getRootLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        contactsRv = (RecyclerView) contentView.findViewById(R.id.contacts_rv);
        recordsBtn = (TextView) contentView.findViewById(R.id.records_btn);
        guideText = (TextView) contentView.findViewById(R.id.guide_text);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.HORIZONTAL, false);
        contactsRv.setLayoutManager(layoutManager);
        mAdapter = new ContactsAdapter();
        contactsRv.setAdapter(mAdapter);
    }

    @Override
    protected void initActions(View contentView) {
        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordsActivity.actionStart(getActivity());
            }
        });
        contentView.findViewById(R.id.home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }
        });
        guideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideStyle2Activity.actionStart(getActivity());
            }
        });
    }

    @Override
    protected void initDatas(View contentView) {
//        if (!GuideSharePreHelper.isGuideShowed(getActivity())) {
//            GuideStyle2Activity.actionStart(getActivity());
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        LogUtils.i("MainFragmentStyle2 onResume isLogining : " + isLogining);
        if (true) { //isLogined, need modify later
        refreshBinderUserList();
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    recordDbService.deleteLimitRecords(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        IntentFilter filter = new IntentFilter(VoipUtils.ACTION_LOGIN_SUCCESS);
        filter.addAction(VoipUtils.ACTION_LOGIN_FAILED);
        getActivity().registerReceiver(loginReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(loginReceiver);
    }

    /**
     * 刷新列表数据
     */
    private void refreshBinderUserList() {
        LogUtils.e("刷新代收好友列表");
        LogUtils.i("refresh binder user list");
//       TXAIAudioSDK.getInstance().getAIAudioFriendList(new TXAIAudioSDK.GetFriendListRspListener() {
//            @Override
//            public void onResult(int i, TXAIAudioFriendInfo[] txaiAudioFriendInfos) {
//                contactsList.clear();
//                contactsList.addAll(parseContacts(txaiAudioFriendInfos));
//                mAdapter.notifyDataSetChanged();
//                if (TXDeviceSDK.mBinderList.size() == 0) {
//                    gotoLogin();
//                }
//            }
//        });

//        TXDeviceBaseManager.getBinderList(new TXDeviceBaseManager.OnGetBinderListListener() {
//            @Override
//            public void onResult(int i, ArrayList<TXBinderInfo> arrayList) {
//                contactsList.clear();
//                contactsList.addAll(parseContacts(arrayList));
//                ContactEntity entity = new ContactEntity();
//                entity.setAddType(1);
//                contactsList.add(entity);
//                mAdapter.notifyDataSetChanged();
//                if (TXDeviceSDK.mBinderList.size() == 0) {
//                    gotoLogin();
//                }
//            }
//        });
        XWDeviceBaseManager.registerBinderRemarkChangeListener(new XWDeviceBaseManager.IGetBinderRemarkListCallback(){
            @Override
            public void onResult(XWBinderRemark[] binderRemarks) {
                LogUtils.e(binderRemarks == null ? "binderRemarks == null" : "binderRemarks length " + binderRemarks.length);
                logVoipRemarks(binderRemarks);
                ArrayList<XWBinderInfo> arrayList = XWDeviceBaseManager.getBinderList();

                LogUtils.e(binderRemarks == null ? "binderList == null" : "binderList length " + arrayList.size());

                for (XWBinderRemark remark : binderRemarks) {
                    for (XWBinderInfo binderInfo : arrayList) {
                        if (binderInfo.tinyID == remark.tinyid) {
                            binderInfo.remark = remark.remark;
                        }
                    }
                }
                contactsList.clear();
                contactsList.addAll(parseContacts(arrayList));
//                ContactEntity entity = new ContactEntity();
//                entity.setAddType(1);
//                contactsList.add(entity);
                mAdapter.notifyDataSetChanged();
                if (arrayList.isEmpty()) {
                    gotoLogin();
                }
            }
        });
    }

    protected void updateData() {
        // 这里XWSDKJNI.getInstance().getDeviceBinderList可以同步取到设备的绑定者，其中可以拿到QQ体系的备注（昵称）
        // getBinderRemarkList是去异步的取绑定者的小微体系的"呼叫备注名"
        XWDeviceBaseManager.getBinderRemarkList(new XWDeviceBaseManager.IGetBinderRemarkListCallback() {
            @Override
            public void onResult(XWBinderRemark[] binderRemarks) {
                ArrayList<XWBinderInfo> arrayList = XWDeviceBaseManager.getBinderList();
                for (XWBinderRemark remark: binderRemarks) {
                    for (XWBinderInfo binderInfo: arrayList) {
                        if (binderInfo.tinyID == remark.tinyid) {
                            binderInfo.remark = remark.remark;
                        }
                    }
                }

                if (arrayList.isEmpty()) {
                    return;
                }

                contactsList.clear();
                XWBinderInfo[] binderArray = new XWBinderInfo[arrayList.size()];
                binderArray = arrayList.toArray(binderArray);
                contactsList.addAll(parseContacts(arrayList));
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void logVoipRemarks(XWBinderRemark[] binderRemarks) {
        StringBuilder sb = new StringBuilder();
        for (XWBinderRemark remark : binderRemarks) {
            sb.append("[remark: " + remark.remark + " tinyid : " + remark.tinyid + "]");
        }
        LogUtils.e(sb.toString());
    }

    private List<ContactEntity> parseContacts(List<XWBinderInfo> arrayList) {
        List<ContactEntity> contactEntities = new ArrayList<>();
        if (arrayList == null || arrayList.isEmpty()) {
            return contactEntities;
        }
        for (XWBinderInfo friend : arrayList) {
            contactEntities.add(parseContact(friend));
        }
        return contactEntities;
    }

  /*  private List<ContactEntity> parseContacts(TXAIAudioFriendInfo[] txaiAudioFriendInfos) {
        List<ContactEntity> contactEntities = new ArrayList<>();
        if (txaiAudioFriendInfos != null) {
            for (TXAIAudioFriendInfo friend : txaiAudioFriendInfos) {
                contactEntities.add(parseContact(friend));
            }
        }
        return contactEntities;
    } */

    private ContactEntity parseContact(XWContactInfo friend) {
        ContactEntity entity = new ContactEntity();
        if (friend == null) {
            return entity;
        }
        entity.setName(friend.remark);
        entity.setIcon(friend.headUrl);
        entity.setUin(friend.tinyID);
        entity.setContactType(friend.contactType);
        entity.setOnLine(friend.online);
        entity.setType(friend.type);
        return entity;
    }

    /**
     * 跳转到登录页面
     */
    private void gotoLogin() {
//        Intent intent = new Intent(getContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        getContext().startActivity(intent);
    }

    @Override
    public void binderUserChanged() {
        LogUtils.i("binderUserChanged");
        refreshBinderUserList();
    }

    @Override
    public void loginOut() {
        gotoLogin();
    }

    class ContactsHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView textView, nameFirstText;
        private ContactEntity entity;

        public ContactsHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            textView = (TextView) itemView.findViewById(R.id.name);
            nameFirstText = (TextView) itemView.findViewById(R.id.name_first_text);
        }

        public void notifyView(final ContactEntity entity) {
            this.entity = entity;
            textView.setText(entity.getName());
            if (TextUtils.isEmpty(entity.getIcon()) && !TextUtils.isEmpty(entity.getName())) {
                nameFirstText.setVisibility(View.VISIBLE);
                nameFirstText.setText(entity.getName().substring(0, 1));
            } else {
                nameFirstText.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityDispatch.startUserInfoPage(getActivity(), entity);
                }
            });
            Glide.with(getActivity()).load(entity.getIcon()).into(icon);
        }
    }

    class AddHolder extends RecyclerView.ViewHolder {

        public AddHolder(View itemView) {
            super(itemView);
        }

        public void notifyView() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuideStyle2Activity.actionStart(v.getContext());
                }
            });
        }
    }

    class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contacts_item_style2, parent, false);
                viewHolder = new ContactsHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contacts_item_add_style2, parent, false);
                viewHolder = new AddHolder(view);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ContactsHolder) {
                ((ContactsHolder) holder).notifyView(contactsList.get(position));
            } else if (holder instanceof AddHolder) {
                ((AddHolder) holder).notifyView();
            }
        }

        @Override
        public int getItemCount() {
            return contactsList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return contactsList.get(position).getAddType();
        }
    }
}
