/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.aispeech.speech;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AILocalWakeupDnnEngine;
import com.aispeech.export.listeners.AILocalWakeupDnnListener;
import com.aispeech.util.SampleConstants;


public class LocalWakeUpDnn extends Activity implements View.OnClickListener {

    final String Tag = this.getClass().getName();
    AILocalWakeupDnnEngine mEngine;
    TextView resultText;
    Button btnStart;
    Button btnStop;


    Toast mToast;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asr);

        resultText = (TextView) findViewById(R.id.text_result);
        resultText.setText("语音唤醒演示:唤醒词是 你好小微");
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_end);

        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        mEngine = AILocalWakeupDnnEngine.createInstance(); //创建实例
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mEngine.setResBin(SampleConstants.RES_WAKEUP); //非自定义唤醒资源可以不用设置words和thresh，资源已经自带唤醒词
//        mEngine.setEchoWavePath("/sdcard/speech"); //保存aec音频到/sdcard/speech/目录,请确保该目录存在
        mEngine.setWords(new String[] {"ni hao xiao wei"});
        mEngine.setThreshold(new float[] {0.12f});
        mEngine.init(this, new AISpeechListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mEngine.setStopOnWakeupSuccess(true);//设置当检测到唤醒词后自动停止唤醒引擎
        mEngine.setUseCustomFeed(true);


        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    @Override
    public void onClick(View v) {
        if (v == btnStart) {
            mEngine.start();
        } else if (v == btnStop) {
            mEngine.stop();
            resultText.setText("已取消！");
        }
    }

    private class AISpeechListenerImpl implements AILocalWakeupDnnListener {

        @Override
        public void onError(AIError error) {
            showTip(error.toString());
        }

        @Override
        public void onInit(int status) {
            Log.i(Tag, "Init result " + status);
            if (status == AIConstant.OPT_SUCCESS) {
                resultText.append("初始化成功!");
                btnStart.setEnabled(true);
                btnStop.setEnabled(true);
            } else {
                resultText.setText("初始化失败!code:" + status);
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            showTip("rmsDB:" + rmsdB);
        }

        @Override
        public void onWakeup(String recordId, double confidence, String wakeupWord) {
            Log.d(Tag,"wakeup foreground");
            resultText.append("唤醒成功  wakeupWord = " + wakeupWord + "  confidence = " + confidence
                    + "\n");
            //在这里启动其他引擎，比如tts或者识别
        }

        @Override
        public void onReadyForSpeech() {
            resultText.append("可以说话了\n");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onRecorderReleased() {
        }

        @Override
        public void onWakeupEngineStopped() {
            mEngine.start();//在这里启动下一轮唤醒
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
            mEngine.destroy();
            mEngine = null;
        }
    }

}
