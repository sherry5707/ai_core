/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.aispeech.speech;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AICloudTTSEngine;
import com.aispeech.export.listeners.AITTSListener;
import com.aispeech.util.Tools;

public class CloudTTS extends Activity implements View.OnClickListener, OnItemSelectedListener {

    final static String CN_PREVIEW = "您好，很高兴认识你";
    final static String EN_PREVIEW = "I want know the past and present of HongKong";

    final String Tag = this.getClass().getName();
    AICloudTTSEngine mEngine;

    TextView tip;
    EditText content;
    Button btnStart, btnStop, btnPause, btnResume, btnClear;
    Spinner spinner_coretype, spinner_res;

    Toast mToast;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_tts);

        tip = (TextView) findViewById(R.id.tip);
        content = (EditText) findViewById(R.id.content);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnResume = (Button) findViewById(R.id.btn_resume);
        btnClear = (Button) findViewById(R.id.btn_clear);
        spinner_coretype = (Spinner) findViewById(R.id.spinner_type);
        spinner_res = (Spinner) findViewById(R.id.spinner_res);

        content.setText(CN_PREVIEW);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnResume.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        spinner_coretype.setOnItemSelectedListener(this);
        spinner_res.setOnItemSelectedListener(this);

        // 创建云端合成播放器
        mEngine = AICloudTTSEngine.createInstance();
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mEngine.setRealBack(true);
        mEngine.init(this, new AITTSListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        // 指定默认中文合成
        mEngine.setLanguage(AIConstant.CN_TTS);

        // 默认女声
        mEngine.setRes(spinner_res.getSelectedItem().toString());
        mEngine.setDeviceId(Util.getIMEI(this));

        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

    }

    @Override
    public void onClick(View v) {
        if (v == btnStart) {
            String refText = content.getText().toString();
            if (spinner_coretype.getSelectedItem().toString().equals("en.syn")) {
                refText = Tools.enFormat(refText);
                mEngine.setLanguage(AIConstant.EN_TTS);
            } else {
                mEngine.setLanguage(AIConstant.CN_TTS);
            }
//            mEngine.setSavePath("/sdcard/"+System.currentTimeMillis() + ".wav");
            mEngine.speak(refText, "1024");
            tip.setText("正在合成...");
        } else if (v == btnStop) {
            mEngine.stop();
            tip.setText("已停止！");
        } else if (v == btnPause) {
            mEngine.pause();
            tip.setText("已暂停!");
        } else if (v == btnResume) {
            mEngine.resume();
            tip.setText("已恢复!");
        } else if (v == btnClear) {
            content.setText("");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> view, View arg1, int arg2, long arg3) {
        if (view == spinner_coretype) {
            String type = spinner_coretype.getSelectedItem().toString();
            if (type.equals("cn.sent.syn")) {
                if(mEngine != null) {
                    mEngine.stop();
                }
                content.setText(CN_PREVIEW);
                mEngine.setLanguage(AIConstant.CN_TTS);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, getResources()
                        .getStringArray(R.array.tts_cn_res));
                spinner_res.setAdapter(spinnerArrayAdapter);
            } else if (type.equals("en.syn")) {
                if(mEngine != null) {
                    mEngine.stop();
                }
                content.setText(EN_PREVIEW);
                mEngine.setLanguage(AIConstant.EN_TTS);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, getResources()
                        .getStringArray(R.array.tts_en_res));
                spinner_res.setAdapter(spinnerArrayAdapter);
            }
        }
        if (view == spinner_res) {
            mEngine.setRes(spinner_res.getSelectedItem().toString());
        }
    }

    private class AITTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            Log.i(Tag, "初始化完成，返回值：" + status);
            if (status == AIConstant.OPT_SUCCESS) {
                tip.setText("初始化成功!");
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
            // TODO Auto-generated method stub

        }

        @Override
        public void onCompletion(String utteranceId) {
            tip.setText("合成完成");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

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
            mEngine.destroy();
            mEngine = null;
        }
    }

}
