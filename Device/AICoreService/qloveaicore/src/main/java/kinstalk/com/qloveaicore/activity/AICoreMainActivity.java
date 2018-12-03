package kinstalk.com.qloveaicore.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//
import com.kinstalk.her.voip.activity.MainActivity;
//import com.tencent.aiaudio.TXSdkWrapper;
//import com.tencent.aiaudio.alarm.SkillAlarmBean;
//import com.tencent.device.TXAIAudioSDK;
//import com.tencent.device.TXDeviceBaseManager;
//import com.tencent.utils.QUtils;

import com.tencent.xiaowei.sdk.XWDeviceBaseManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

//import de.greenrobot.event.EventBus;
//import kinstalk.com.common.BusEventCommon;
//import kinstalk.com.cooker.CookerTaskManager;
//import kinstalk.com.cooker.ICookerTaskManager;
//import kinstalk.com.cooker.db.DbCore;
//import kinstalk.com.cooker.entity.CookerTaskBean;
//import kinstalk.com.qaicore.QAIUtils;
//import kinstalk.com.qaicore.convertor.ConvertorConstant;
//import kinstalk.com.qaicore.convertor.QAIProtocolConvertor;
import kinstalk.com.common.BusEventCommon;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.qloveaicore.IAICoreInterface;
import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.R;
//import kinstalk.com.utils.AiCoreConstants;
//import kinstalk.com.utils.JsonUtils;
//import kinstalk.com.utils.QAILog;
import kinstalk.com.qloveaicore.txsdk.TXOpenSdkWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AICoreMainActivity extends Activity {
    public static final String TAG = "AI-AICoreMainActivity";
    private static final int MSG_GENERATE = 0x100;
    private static final int MSG_BIND_STATE_CHANGE = 0x101;
    private boolean qrCodeShown = false;
    private ImageView mImageV;
    private ImageView mImageAvatar;
    private TextView mTextV;
    private Handler mWHandler;
    private EditText mTTSEditText;

    /* TODO For keep connected
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            // TODO Auto-generated method stub
            if (mIAICoreInterface == null)
                return;
            mIAICoreInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mIAICoreInterface = null;
            // 重新绑定远程服务
            bindService(QAIUtils.getAICoreServiceIntent(), conn, Service.BIND_AUTO_CREATE);
        }
    };*/
    private IAICoreInterface mIAICoreInterface = null;
    private ServiceConnection conn = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            QAILog.d(TAG," onServiceConnected");
            /*TODO For keep connected
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            mIAICoreInterface = IAICoreInterface.Stub.asInterface(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            QAILog.d(TAG," onServiceDisconnected");
            mIAICoreInterface = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        QAILog.d(TAG, "onCreate: Enter");
        super.onCreate(savedInstanceState);
        startService(QAIUtils.getAICoreServiceIntent());

        setContentView(R.layout.activity_aicore_main);

        initView();
        Toast.makeText(this, "亲见智能服务已启动", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().register(this);
        bindService(QAIUtils.getAICoreServiceIntent(), conn, Service.BIND_AUTO_CREATE);
    }

    private void initView() {
        mImageV = (ImageView) findViewById(R.id.iv_qrcode);
        mImageAvatar = (ImageView) findViewById(R.id.iv_qq_head);
        mTextV = (TextView) findViewById(R.id.tv_login);
        mTTSEditText = (EditText) findViewById(R.id.tts_text);
        HandlerThread h = new HandlerThread("generate_qr_code");
        h.start();
        mWHandler = new Handler(h.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GENERATE:
                        String pid = String.valueOf(TXOpenSdkWrapper.pid);
                        String sn = TXOpenSdkWrapper.sn;
                        final Bitmap b = QAIUtils.createImage(XWDeviceBaseManager.getQRCodeUrl());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!qrCodeShown) {
                                    qrCodeShown = true;
                                    mImageV.setImageBitmap(b);
                                }
                            }
                        });
                        break;
                    case MSG_BIND_STATE_CHANGE:
                        boolean bind = msg.arg1 == 1;
                        String headUrl = (String) msg.obj;
                        if (bind) {
                            final Bitmap bmp = getImage(headUrl);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (bmp != null) mImageAvatar.setImageBitmap(bmp);
                                }
                            });
                        } else {
                            mWHandler.sendMessage(mWHandler.obtainMessage(MSG_GENERATE));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mImageAvatar.setImageBitmap(null);
                                }
                            });
                        }
                        break;
                }
                return false;
            }
        });

        //TODO for voip main activity
        findViewById(R.id.btn_vedio_contacts).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_unbind).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AICoreMainActivity.this)
                        .setMessage("确定要解除绑定吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               /* TXDeviceBaseManager.eraseAllBinders(new TXDeviceBaseManager.OnEraseAllBinderListener() {
                                    @Override
                                    public void onResult(int i) {
                                        Toast.makeText(AICoreMainActivity.this, "解绑成功！", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (TXSdkWrapper.isLogined) {
            mTextV.setText("already login");
            mImageV.setVisibility(View.INVISIBLE);
        } else */
        {
            if (!qrCodeShown) {
                mImageV.setVisibility(View.VISIBLE);
                mWHandler.sendMessage(mWHandler.obtainMessage(MSG_GENERATE));
                mTextV.setText("使用手机QQ扫一扫绑定");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(conn);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BusEventCommon evt) {
        QAILog.d(TAG, "onEventMainThread: Enter, " + evt);
        if (evt.eventType == BusEventCommon.BIND_STATE_CHANGE) {
            mWHandler.obtainMessage(MSG_BIND_STATE_CHANGE, evt.bVar1 ? 1 : 0, 0, evt.strVar1).sendToTarget();
        }
    }

    private Bitmap getImage(String url) {
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(url).build();
        Response rsp;
        try {
            rsp = client.newCall(req).execute();
            if (rsp.isSuccessful()) {
                InputStream is = rsp.body().byteStream();
                return BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void RequestQcardLaunchOnClick(View v) {
        Log.d(TAG, "GetQcardLaunchOnClick");
        EditText qcardET = (EditText) findViewById(R.id.et_qcard_content);
        String qcardData = qcardET.getText().toString();
        if (TextUtils.isEmpty(qcardData)) {
            Toast.makeText(this, "请输入qcrad指令", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("service", "qcard");
            jsonObj.put("opcode", "request_text");
            jsonObj.put("data", qcardData);
            jsonObj.put("play_skill", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestParams = jsonObj.toString();
        Log.d(TAG, "GetQcardLaunchOnClick : requestParams " + requestParams);

        if (mIAICoreInterface != null) {
            try {
                mIAICoreInterface.requestData(requestParams);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
