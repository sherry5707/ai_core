package com.kinstalk.her.voip.ui.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kinstalk.her.voip.R;
import com.kinstalk.her.voip.activity.ContactsAddActivity;
import com.kinstalk.her.voip.activity.GuideActivity;
import com.kinstalk.her.voip.activity.dispatch.ActivityDispatch;
import com.kinstalk.her.voip.model.entity.ContactEntity;
import com.kinstalk.her.voip.recevier.NotifyReceiver;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;
//import com.tencent.device.TXDeviceSDK;
import com.tencent.xiaowei.info.XWBinderInfo;
import com.tencent.xiaowei.info.XWBinderRemark;
import com.tencent.xiaowei.info.XWContactInfo;


import java.util.ArrayList;
import java.util.List;

//import static com.tencent.aiaudio.CommonApplication.ACTION_ON_ERASE_ALL_BINDERS;
//import static com.tencent.aiaudio.CommonDef.ACTION_ON_RECEIVE_REQUEST_ADD_FRIEND;

public class ContactsFragment extends BaseFragment implements NotifyReceiver.NotifyDelegate{

    private RecyclerView contactsRv;
    private Button contactsAddBtn;
    private TextView guideText;
    private ContactsAdapter mAdapter;

    private NotifyReceiver notifyReceiver;

    private List<ContactEntity> contactsList = new ArrayList<>();
    private static final String ACTION_ON_BINDER_LIST_CHANGE = "BinderListChange";
    // to do, import this from QAICORE project;

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Override
    protected void initBaseDatas(Bundle savedInstanceState) {
        super.initBaseDatas(savedInstanceState);
        notifyReceiver = new NotifyReceiver();
        notifyReceiver.setNotifyDelegate(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ON_BINDER_LIST_CHANGE);
//        filter.addAction(CommonDef.ACTION_ON_FRIEND_LIST_CHANGE);
//        filter.addAction(ACTION_ON_ERASE_ALL_BINDERS);
//        filter.addAction(ACTION_ON_RECEIVE_REQUEST_ADD_FRIEND);
//        filter.addAction(CommonDef.ACTION_ON_RECEIVE_MSG);
        getContext().registerReceiver(notifyReceiver, filter);
    }

    @Override
    public void onDestroy() {
        if (notifyReceiver != null) {
            getContext().unregisterReceiver(notifyReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected int getRootLayoutRes() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        contactsRv = (RecyclerView) contentView.findViewById(R.id.contacts_rv);
        contactsAddBtn = (Button) contentView.findViewById(R.id.contacts_add_btn);
        guideText = (TextView) contentView.findViewById(R.id.guide_text);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.HORIZONTAL, false);
        contactsRv.setLayoutManager(layoutManager);
        mAdapter = new ContactsAdapter();
        contactsRv.setAdapter(mAdapter);
    }

    @Override
    protected void initActions(View contentView) {
        contactsAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsAddActivity.actionStart(getActivity());
            }
        });
        guideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideActivity.actionStart(getActivity());
            }
        });
    }

    @Override
    protected void initDatas(View contentView) {

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBinderUserList();
    }

    /**
     * 刷新列表数据
     */
    private void refreshBinderUserList() {
        LogUtils.e("刷新代收好友列表");
//        TXAIAudioSDK.getInstance().getAIAudioFriendList(new TXAIAudioSDK.GetFriendListRspListener() {
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
//                mAdapter.notifyDataSetChanged();
//                if (TXDeviceSDK.mBinderList.size() == 0) {
//                    gotoLogin();
//                }
//            }
//        });

        XWDeviceBaseManager.getBinderRemarkList(new XWDeviceBaseManager.IGetBinderRemarkListCallback() {
            @Override
            public void onResult(XWBinderRemark[] binderRemarks) {
                LogUtils.e(binderRemarks == null ? "binderRemarks == null" : "binderRemarks length " + binderRemarks.length);
                ArrayList<XWBinderInfo> arrayList = XWDeviceBaseManager.getBinderList();

                for (XWBinderRemark remark: binderRemarks) {
                    for (XWBinderInfo binderInfo: arrayList) {
                        if (binderInfo.tinyID == remark.tinyid) {
                            binderInfo.remark = remark.remark;
                        }
                    }
                }
                contactsList.clear();
                contactsList.addAll(parseContacts(arrayList));
                mAdapter.notifyDataSetChanged();
                if (arrayList.isEmpty()) {
                    gotoLogin();
                }
            }
        });
    }

    private List<ContactEntity> parseContacts(List<XWBinderInfo> arrayList){
        List<ContactEntity> contactEntities = new ArrayList<>();
        if (arrayList == null || arrayList.isEmpty()) {
            return contactEntities;
        }
        for (XWBinderInfo friend : arrayList) {
            contactEntities.add(parseContact(friend));
        }
        return contactEntities;
    }

 /*   private List<ContactEntity> parseContacts(TXAIAudioFriendInfo[] txaiAudioFriendInfos) {
        List<ContactEntity> contactEntities = new ArrayList<>();
        if (txaiAudioFriendInfos != null) {
            for (TXAIAudioFriendInfo friend : txaiAudioFriendInfos) {
                contactEntities.add(parseContact(friend));
            }
        }
        return contactEntities;
    }
*/
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
        refreshBinderUserList();
    }

    @Override
    public void loginOut() {
        gotoLogin();
    }

    class ContactsHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView textView;
        private ContactEntity entity;

        public ContactsHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            textView = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityDispatch.startUserInfoPage(getActivity(), entity);
//                    ActivityDispatch.call(getActivity(), entity);
                }
            });
        }

        public void notifyView(ContactEntity entity) {
            this.entity = entity;
            textView.setText(entity.getName());
            Glide.with(getActivity())
                    .load(entity.getIcon())
                    .placeholder(new ColorDrawable(Color.WHITE))
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource,
                                                    GlideAnimation<? super GlideDrawable> glideAnimation) {
                            icon.setImageDrawable(resource);
                        }
                    });

        }
    }

    class ContactsAdapter extends RecyclerView.Adapter<ContactsHolder> {

        @Override
        public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contacts_item, parent, false);
            ContactsHolder contactsHolder = new ContactsHolder(view);
            return contactsHolder;
        }

        @Override
        public void onBindViewHolder(ContactsHolder holder, int position) {
            holder.notifyView(contactsList.get(position));
        }

        @Override
        public int getItemCount() {
            return contactsList.size();
        }
    }




    /**
     * 判断一个Service是否正在运行
     * <p/>
     * 注意：如果服务正在重启，也会返回true
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isServiceRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            if (serviceInfo.service.getClassName().equals(serviceName) && serviceInfo.service.getPackageName().equals(context.getPackageName()) && serviceInfo.started) {
                isServiceRunning = true;
                break;
            }
        }
        return isServiceRunning;
    }

}
