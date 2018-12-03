/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.aispeech.speech;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalTTSEngine;
import com.aispeech.export.listeners.AITTSListener;
import com.aispeech.util.SampleConstants;

public class LocalTTS extends Activity implements OnClickListener {

    final static String CN_PREVIEW = "输入;";
    final String Tag = this.getClass().getName();


    TextView tip;
    EditText content;
    Button btnStart, btnPlayerPause, btnPlayerResume, btnPlayerStop;
    String resDir;

    Toast mToast;

    AILocalTTSEngine mEngine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_tts);

        tip = (TextView) findViewById(R.id.tip);
        content = (EditText) findViewById(R.id.content);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPlayerPause = (Button) findViewById(R.id.btn_pause);
        btnPlayerResume = (Button) findViewById(R.id.btn_resume);
        btnPlayerStop = (Button) findViewById(R.id.btn_stop);
        content.setText(CN_PREVIEW);

        btnStart.setEnabled(false);
        btnPlayerPause.setOnClickListener(this);
        btnPlayerResume.setOnClickListener(this);
        btnPlayerStop.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        initEngine();
    }

    private void initEngine() {
        if (mEngine != null) {
            mEngine.destroy();
        }
        mEngine = AILocalTTSEngine.createInstance();//创建实例
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mEngine.setResource(SampleConstants.RES_TTS);
        mEngine.setDictDbName(SampleConstants.RES_DICT);
        mEngine.init(this, new AILocalTTSListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);//初始化合成引擎
        mEngine.setSpeechRate(0.85f);//设置语速
        mEngine.setDeviceId(Util.getIMEI(this));
    }


    int mIndex = 0;
    @Override
    public void onClick(View v) {
        if (v == btnStart) {
            String refText = content.getText().toString();

            if (!TextUtils.isEmpty(refText)) {
                if (mEngine != null) {
                    mEngine.setSavePath(Environment.getExternalStorageDirectory() + "/tts/"
                            + System.currentTimeMillis() + ".wav");
                    mEngine.speak(refText, "1024");
                }
                tip.setText("正在合成...");
            } else {
                tip.setText("没有合法文本");
            }
        } else if (v == btnPlayerPause) {
            if (mEngine != null) {
                mEngine.pause();
            }

        } else if (v == btnPlayerResume) {
            if (mEngine != null) {
                mEngine.resume();
            }

        } else if (v == btnPlayerStop) {
            tip.setText("合成已停止");
            if (mEngine != null) {
                mEngine.stop();
            }
        }
    }

    private class AILocalTTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            Log.i(Tag, "初始化完成，返回值：" + status);
            Log.i(Tag, "onInit");
            if (status == AIConstant.OPT_SUCCESS) {
                tip.setText("初始化成功!");
                btnStart.setEnabled(true);
            } else {
                tip.setText("初始化失败!code:" + status);
            }
        }

        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
            showTip("当前:" + currentTime + "ms, 总计:" + totalTime + "ms, 可信度:" + isRefTextTTSFinished);
        }

        @Override
        public void onError(String utteranceId, AIError error) {
            tip.setText("检测到错误");
            content.setText(content.getText() + "\nError:\n" + error.toString());
        }

        @Override
        public void onReady(String utteranceId) {
            LocalTTS.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Thread.currentThread().setName("TTS_PLAY");
                    tip.setText("开始播放");
                    Log.i(Tag, "onReady");
                }
            });
        }

        @Override
        public void onCompletion(String utteranceId) {
            LocalTTS.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Thread.currentThread().setName("TTS_completed");
                    tip.setText("合成完成");
                    Log.i(Tag, "onCompletion");
                }
            });
        }
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
    public void onStop() {
        super.onStop();
        if (mEngine != null) {
            mEngine.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            Log.i(Tag, "release in LocalTTS");
            mEngine.destroy();
            mEngine = null;
        }
    }

}
