/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.aispeech.speech;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;

import com.aispeech.util.GrammarHelper;
import com.aispeech.util.SampleConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 本示例将演示通过联合使用本地识别引擎和本地语法编译引擎实现定制识别。<br>
 * 将由本地语法编译引擎根据手机中的联系人和应用列表编译出可供本地识别引擎使用的资源，从而达到离线定制识别的功能。
 */
public class LocalGrammar extends Activity implements OnClickListener{
    public static final String TAG = LocalGrammar.class.getName();




    EditText tv;
    Button bt_res;
    Button bt_asr;
    Toast mToast;

    AILocalGrammarEngine mGrammarEngine;
    AIMixASREngine mAsrEngine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.grammar);
        tv = (EditText) findViewById(R.id.tv);
        bt_res = (Button) findViewById(R.id.btn_gen);
        bt_asr = (Button) findViewById(R.id.btn_asr);
        bt_res.setEnabled(false);
        bt_asr.setEnabled(false);
        bt_res.setOnClickListener(this);
        bt_asr.setOnClickListener(this);

        initGrammarEngine();
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }




    /**
     * 初始化资源编译引擎
     */
    private void initGrammarEngine() {
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
        }
        Log.i(TAG, "grammar create");
        mGrammarEngine = AILocalGrammarEngine.createInstance();
//        mGrammarEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mGrammarEngine.setResFileName(SampleConstants.RES_VAD);
        mGrammarEngine
                .init(this, new AILocalGrammarListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mGrammarEngine.setDeviceId(Util.getIMEI(this));
    }

    /**
     * 初始化混合引擎
     */
    @SuppressLint("NewApi")
    private void initAsrEngine() {
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
        }
        Log.i(TAG, "asr create");
        mAsrEngine = AIMixASREngine.createInstance();
//        mAsrEngine.setResStoragePath("/sdcard/aispeech/");////设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mAsrEngine.setResBin(SampleConstants.RES_EBNFR);
        mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);

        mAsrEngine.setVadResource(SampleConstants.RES_VAD);
        mAsrEngine.setServer(SampleConstants.SERVER_GRAY);
        mAsrEngine.setRes(SampleConstants.RES_AIHOME);
        mAsrEngine.setUseXbnfRec(true);
        mAsrEngine.setUseForceout(false);
        mAsrEngine.setAthThreshold(0.6f);
        mAsrEngine.setIsRelyOnLocalConf(true);
        mAsrEngine.setIsPreferCloud(false);
        mAsrEngine.setLocalBetterDomains(new String[] { "aihome"});
//        mAsrEngine.setCloudNotGoodAtDomains(new String[]{"phonecall","weixin"});
//        mAsrEngine.putCloudLocalDomainMap("weixin", "wechat");
//        mAsrEngine.putCloudLocalDomainMap("phonecall", "phone");
        mAsrEngine.setWaitCloudTimeout(5000);
        mAsrEngine.setPauseTime(200);
        mAsrEngine.setUseConf(true);
//        mAsrEngine.setVersion("1.0.4"); //设置资源的版本号
        mAsrEngine.setNoSpeechTimeOut(0);
        mAsrEngine.setDeviceId(Util.getIMEI(this));
        mAsrEngine.setCloudVadEnable(false);
        mAsrEngine.init(this, new AIASRListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mAsrEngine.setUseCloud(true);//该方法必须在init之后
        
//        mAsrEngine.setUserId("AISPEECH"); //填公司名字
        
//        mAsrEngine.setCoreType("cn.sds"); //cn.sds为云端对话服务，cn.dlg.ita为云端语义服务，默认为云端语义,想要访问对话服务时，才设置为cn.sds，否则不用设置
    }

    /**
     * 开始生成识别资源
     */

    private void startResGen() {
        // 生成ebnf语法
        GrammarHelper gh = new GrammarHelper(this);
        String contactString = gh.getConatcts();
        contactString = "";
        String appString = gh.getApps();
        // 如果手机通讯录没有联系人
        if (TextUtils.isEmpty(contactString)) {
            contactString = "无联系人";
        }
        String ebnf = gh.importAssets(contactString, "", SampleConstants.RES_GRAMMAR);
        Log.i(TAG, ebnf);
        // 设置ebnf语法
        mGrammarEngine.setEbnf(ebnf);
        // 启动语法编译引擎，更新资源
        mGrammarEngine.update();
    }

    /**
     * 语法编译引擎回调接口，用以接收相关事件
     */
    public class AILocalGrammarListenerImpl implements AILocalGrammarListener {

        @Override
        public void onError(AIError error) {
            showInfo("资源生成发生错误");
            showTip(error.getError());
            setResBtnEnable(true);
        }

        @Override
        public void onUpdateCompleted(String recordId, String path) {
            showInfo("资源生成/更新成功\npath=" + path + "\n重新加载识别引擎...");
            Log.i(TAG, "资源生成/更新成功\npath=" + path + "\n重新加载识别引擎...");
            initAsrEngine();
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                showInfo("资源定制引擎加载成功");
                if (mAsrEngine == null) {
                    setResBtnEnable(true);
                }
            } else {
                showInfo("资源定制引擎加载失败");
            }
        }
    }


    /**
     * 识别引擎回调接口，用以接收相关事件
     */
    public class AIASRListenerImpl implements AIASRListener {

        @Override
        public void onBeginningOfSpeech() {
            showInfo("检测到说话");

        }

        @Override
        public void onEndOfSpeech() {
            showInfo("检测到语音停止，开始识别...");
        }

        @Override
        public void onReadyForSpeech() {
            showInfo("请说话...");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            showTip("RmsDB = " + rmsdB);
        }

        @Override
        public void onError(AIError error) {
            showInfo("识别发生错误");
            showTip(error.getErrId() + "");
            setAsrBtnState(true, "识别");
        }

        @Override
        public void onResults(AIResult results) {
            Log.i(TAG, results.getResultObject().toString());
            try {
                showInfo(new JSONObject(results.getResultObject().toString()).toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setAsrBtnState(true, "识别");
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                Log.i(TAG, "end of init asr engine");
                showInfo("识别引擎加载成功");
                setResBtnEnable(true);
                setAsrBtnState(true, "识别");
            } else {
                showInfo("识别引擎加载失败");
            }
        }

        @Override
        public void onRecorderReleased() {
            // showInfo("检测到录音机停止");
        }

		@Override
		public void onBufferReceived(byte[] buffer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNotOneShot() {
			// TODO Auto-generated method stub
			
		}
    }

    /**
     * 设置资源按钮的状态
     * 
     * @param state
     *            使能状态
     */
    private void setResBtnEnable(final boolean state) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                bt_res.setEnabled(state);
            }
        });
    }

    /**
     * 设置识别按钮的状态
     * 
     * @param state
     *            使能状态
     * @param text
     *            按钮文本
     */
    private void setAsrBtnState(final boolean state, final String text) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                bt_asr.setEnabled(state);
                bt_asr.setText(text);
            }
        });
    }

    private void showInfo(final String str) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tv.setText(str);
            }
        });
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGrammarEngine != null) {
            Log.i(TAG, "grammar cancel");
            mGrammarEngine.cancel();
        }
        if (mAsrEngine != null) {
            Log.i(TAG, "asr cancel");
            mAsrEngine.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGrammarEngine != null) {
            Log.i(TAG, "grammar destroy");
            mGrammarEngine.destroy();
            mGrammarEngine = null;
        }
        if (mAsrEngine != null) {
            Log.i(TAG, "asr destroy");
            mAsrEngine.destroy();
            mAsrEngine = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == bt_res) {
            setResBtnEnable(false);
            setAsrBtnState(false, "识别");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showInfo("开始生成资源...");
                    startResGen();
                }
            }).start();
        } else if (view == bt_asr) {
            if ("识别".equals(bt_asr.getText())) {
                if (mAsrEngine != null) {
                    setAsrBtnState(true, "停止");
                    mAsrEngine.start();
                } else {
                    showTip("请先生成资源");
                }
            } else if ("停止".equals(bt_asr.getText())) {
                if (mAsrEngine != null) {
                    setAsrBtnState(true, "识别");
                    mAsrEngine.cancel();
                }
            }
        }
    }

}