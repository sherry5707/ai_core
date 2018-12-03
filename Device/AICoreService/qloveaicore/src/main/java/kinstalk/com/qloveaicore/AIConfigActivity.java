package kinstalk.com.qloveaicore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.xiaowei.sdk.XWSDKJNI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.qloveaicore.api.Api;
import kinstalk.com.qloveaicore.txsdk.ConfigApiHelper;

/**
 * Created by majorxia on 2018/3/30.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */
public class AIConfigActivity extends Activity {
    private static final String TAG = "AICoreConfig-Main";
    private CheckBox mIdCbUseSeparateEggApp;
    private Button mIdBtnExit;
    private CheckBox idCbEnableContinousSpeaking;
    private CheckBox mIdCbEnableBtDemo;

    private CheckBox idCbDumpCallBack;
    private CheckBox idCbDumpEveryVoice;
    private CheckBox idCbDumpVoice;
    private CheckBox idCbDumpTts;
    private Spinner idChooseApi;
    private List<String> apiList;
    private ArrayAdapter<String> arrayAdapter;
    private HashMap<String, String> apiMap;
    private String apiKeyProd = "PRODUCT";
    private String apiKeyTest = "TEST";
    private String apiKeyDev = "DEV";
    private EditText idETSearchIndex;
    private EditText idETSearchThreshold;
    private CheckBox idCbWakeupOnly;
    private CheckBox idCbAiPrompt;
    private CheckBox idCbDumpSnsryWakeupAudio;
    private CheckBox idCbSnsryEnable;
    private CheckBox idCbSpeechEnable;
    private CheckBox idCbEnableTXSdkLog;
    private CheckBox idCbWakeupFailureDebug;
    private CheckBox idCbUseTestApp;
    private CheckBox idCbSaveTTSFile;

    private Button mBinBt;
    private TextView mBinCt;
    private Button mSdkBt;
    private TextView mSdkCt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);
        assignViews();
    }

    private void assignViews() {
        mIdCbUseSeparateEggApp = (CheckBox) findViewById(R.id.id_cb_use_separate_eggApp);
        mIdBtnExit = (Button) findViewById(R.id.id_btn_exit);
        idCbEnableContinousSpeaking = (CheckBox) findViewById(R.id.id_cb_enable_continous_speaking);
        mIdCbEnableBtDemo = (CheckBox) findViewById(R.id.id_cb_enable_bt_sco_demo);
        idCbDumpCallBack  = (CheckBox) findViewById(R.id.id_cb_dump_callback);
        idCbDumpEveryVoice = (CheckBox) findViewById(R.id.id_cb_dump_every_voice);
        idCbDumpVoice = (CheckBox) findViewById(R.id.id_cb_dump_voice);
        idCbDumpTts = (CheckBox) findViewById(R.id.id_cb_dump_tts);
        idChooseApi = (Spinner) findViewById(R.id.id_chos_api_spinner);
        idETSearchIndex = (EditText) findViewById(R.id.id_et_search_index);
        idETSearchThreshold = (EditText) findViewById(R.id.id_et_search_threshold);
        idCbWakeupOnly = (CheckBox) findViewById(R.id.id_cb_snsry_wakeup_only);
        idCbAiPrompt = (CheckBox) findViewById(R.id.id_cb_enable_ai_prompt);
        idCbDumpSnsryWakeupAudio = (CheckBox) findViewById(R.id.id_cb_snsry_dump_wakeup_audio);
        idCbSnsryEnable = (CheckBox) findViewById(R.id.id_cb_snsry_enable);
        idCbSpeechEnable = (CheckBox) findViewById(R.id.id_cb_speech_enable);
        idCbEnableTXSdkLog = (CheckBox) findViewById(R.id.id_cb_enable_sdk_log);
        idCbWakeupFailureDebug = (CheckBox) findViewById(R.id.id_wakeup_failure_test);
        idCbUseTestApp = (CheckBox) findViewById(R.id.id_cb_use_test_service_type);
        idCbSaveTTSFile = (CheckBox) findViewById(R.id.id_cb_save_tts_voice_file);
        //add by zhangyawen for get Din number [Begin]
        mBinBt = (Button)findViewById(R.id.id_get_bin_bt);
        mBinCt = (TextView)findViewById(R.id.id_get_bin_ct);
        mBinBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long bin = XWSDKJNI.getSelfDin();
                String binStr = getResources().getString(R.string.get_text_content);
                String result = String.format(binStr,bin+"");
                mBinCt.setText(result);
                Log.d(TAG,""+result);
            }
        });
        //add by zhangyawen for get Din number [End]

        //add by zhangyawen for get SDK number [Begin]
        mSdkBt = (Button)findViewById(R.id.id_get_sdk_bt);
        mSdkCt = (TextView)findViewById(R.id.id_get_sdk_ct);
        mSdkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] sdkVersion = XWSDKJNI.getInstance().getSDKVersion();

                String sdkStr = getResources().getString(R.string.get_text_sdk_content);
                String info = "";
                for (int i = 0; i < sdkVersion.length; i++) {
                    if (TextUtils.isEmpty(info)) {
                        info =""+sdkVersion[i];
                    }else{
                        info =info + "." + sdkVersion[i];
                    }
                }
                String result = String.format(sdkStr,info+"");
                mSdkCt.setText(result);
                Log.d(TAG,""+result);
            }
        });
        //add by zhangyawen for get SDK number [End]

        mIdCbUseSeparateEggApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "onCheckedChanged: isChecked," + isChecked);
                Intent intent = new Intent();
                intent.setAction(AICoreDef.INTENT_AICORE_DEBUG_SETTING);
                intent.putExtra(AICoreDef.INTENT_AICORE_DEBUG_EXTRA_EGG_APP, isChecked);
                AIConfigActivity.this.sendBroadcast(intent);
            }
        });

        idCbEnableContinousSpeaking.setChecked(QAIConfigController.readEnableContinuousSpeaking(this));
        idCbEnableContinousSpeaking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbEnableContinousSpeaking onCheckedChanged: isChecked," + isChecked);
                QAIConfigController.writeEnableContinuousSpeaking(AIConfigActivity.this, isChecked);
            }
        });

        mIdCbEnableBtDemo.setChecked(QAIConfigController.readEnableBTScoDemo(this));
        mIdCbEnableBtDemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "mIdCbEnableBtDemo on checked changed:", isChecked);
                QAIConfigController.writeEnableBTScoDemo(AIConfigActivity.this, isChecked);
            }
        });

        if (SystemTool.isAudioDumpTest) {
            idCbDumpEveryVoice.setChecked(true);
            idCbDumpEveryVoice.setEnabled(false);

            idCbDumpVoice.setChecked(true);
            idCbDumpVoice.setEnabled(false);

            idCbDumpCallBack.setChecked(true);
            idCbDumpCallBack.setEnabled(false);
        } else {
            idCbDumpEveryVoice.setEnabled(true);
            idCbDumpVoice.setEnabled(true);
            idCbDumpCallBack.setEnabled(true);

            idCbDumpCallBack.setChecked(QAIConfigController.readEnable(this, QAIConstants.SP_KEY_DUMP_CALLBACK));
            idCbDumpCallBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    QAILog.d(TAG, "idCbDumpCallBack onCheckedChanged, isChecked: " + isChecked);
                    QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SP_KEY_DUMP_CALLBACK, isChecked);
                    // TODO TXSdkWrapper.configDump();
                    // TODO QAILog.d(TAG, "TXAIAudioSDK.dump: ", TXAIAudioSDK.dump);
                }
            });

            idCbDumpEveryVoice.setChecked(QAIConfigController.readEnable(this, QAIConstants.SP_KEY_DUMP_EVERY_VOICE));
            idCbDumpEveryVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    QAILog.d(TAG, "idCbDumpEveryVoice onCheckedChanged, isChecked: " + isChecked);
                    QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SP_KEY_DUMP_EVERY_VOICE, isChecked);
                    // TODO TXOpenSdkWrapper.configDump();
                    // TODO QAILog.d(TAG, "TXAIAudioSDK.dump: ", TXAIAudioSDK.dump);
                }
            });

            idCbDumpVoice.setChecked(QAIConfigController.readEnable(this, QAIConstants.SP_KEY_DUMP_VOICE));
            idCbDumpVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    QAILog.d(TAG, "idCbDumpVoice onCheckedChanged, isChecked : ", isChecked);
                    QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SP_KEY_DUMP_VOICE, isChecked);
                    // TODO TXSdkWrapper.configDump();
                    // TODO QAILog.d(TAG, "TXAIAudioSDK.dump: ", TXAIAudioSDK.dump);
                }
            });
        }

        if (SystemTool.isAudioDumpTest) {
            idCbDumpTts.setChecked(true);
            idCbDumpTts.setEnabled(false);
        } else {
            idCbDumpTts.setEnabled(true);

            idCbDumpTts.setChecked(QAIConfigController.readEnable(this, QAIConstants.SP_KEY_DUMP_TTS));
            idCbDumpTts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    QAILog.d(TAG, "idCbDumpTTS onCheckedChanged, isChecked: " + isChecked);
                    QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SP_KEY_DUMP_TTS, isChecked);
                    if (!SystemTool.isUserType()) {
                        // TODO Player.isDumpTts = isChecked;
                        // TODO QAILog.d(TAG, "TXAIAudioSDK.dumpTTS: ", Player.isDumpTts);
                    }
                }
            });
        }
        idCbAiPrompt.setChecked(QAIConfigController.readEnable(this, QAIConstants.SP_KEY_AI_PROMPT));
        idCbAiPrompt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbAiPrompt onCheckedChanged, isChecked: " + isChecked);
                QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SP_KEY_AI_PROMPT, isChecked);
                QAIConfig.isAiPromptEnable = isChecked;
            }
        });


        String search_index = String.valueOf(QAIConfigController.readSearchIndex(AIConfigActivity.this));
        idETSearchIndex.setText(search_index);



        idCbWakeupOnly.setChecked(QAIConfigController.readSnsryWakeupOnlyDbg(this));
        idCbWakeupOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbWakeupOnly onCheckedChanged: isChecked," + isChecked);
                QAIConfigController.writeSnsryWakeupOnlyDbg(AIConfigActivity.this, isChecked);
            }
        });

        idCbDumpSnsryWakeupAudio.setChecked(QAIConfigController.readSnsryWakeupDumpAudio(this));
        idCbDumpSnsryWakeupAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbSnsry dump wakeup audio onCheckedChanged: isChecked," + isChecked);
                QAIConfigController.writeSnsryWakeupDumpAudio(AIConfigActivity.this, isChecked);
            }
        });

        idCbSnsryEnable.setChecked(QAIConfigController.readSnsryEnable(this));
        idCbSnsryEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbSnsry snsry enable onCheckedChanged: isChecked," + isChecked);
                QAIConfigController.writeSnsryEnable(AIConfigActivity.this, isChecked);
                if(isChecked == true){
                    idCbSpeechEnable.setChecked(false);
                    QAILog.d(TAG,"idCbSpeechEnable changed,isChecked:",false);
                    QAIConfigController.writeSpeechEnable(AIConfigActivity.this, false);


                    String search_threshold = String.valueOf(QAIConfigController.readSnsryWakeupThreshold(AIConfigActivity.this));
                    idETSearchThreshold.setText(search_threshold);
                    idETSearchIndex.setVisibility(View.VISIBLE);
                    findViewById(R.id.id_et_search_index_txt).setVisibility(View.VISIBLE);
                    idETSearchThreshold.setVisibility(View.VISIBLE);
                    findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.VISIBLE);

                }
                else {
                    idETSearchIndex.setVisibility(View.INVISIBLE);
                    findViewById(R.id.id_et_search_index_txt).setVisibility(View.INVISIBLE);
                    idETSearchThreshold.setVisibility(View.INVISIBLE);
                    findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.INVISIBLE);

                }
            }
        });



        idCbSpeechEnable.setChecked(QAIConfigController.readSpeechEnable(this));
        idCbSpeechEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbSpeech speech enable onCheckedChanged: isChecked," + isChecked);
                QAIConfigController.writeSpeechEnable(AIConfigActivity.this, isChecked);
                if(isChecked == true){
                    idCbSnsryEnable.setChecked(false);
                    QAILog.d(TAG,"idCbSnsryEnable changed,isChecked:",false);
                    QAIConfigController.writeSnsryEnable(AIConfigActivity.this, false);

                    String search_threshold = String.valueOf(QAIConfigController.readSpeechWakeupThreshold(AIConfigActivity.this));
                    idETSearchThreshold.setText(search_threshold);
                    idETSearchIndex.setVisibility(View.INVISIBLE);
                    findViewById(R.id.id_et_search_index_txt).setVisibility(View.INVISIBLE);
                    idETSearchThreshold.setVisibility(View.VISIBLE);
                    findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.VISIBLE);
                }
                else {
                    idETSearchIndex.setVisibility(View.INVISIBLE);
                    findViewById(R.id.id_et_search_index_txt).setVisibility(View.INVISIBLE);
                    idETSearchThreshold.setVisibility(View.INVISIBLE);
                    findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.INVISIBLE);

                }
            }
        });



        idCbWakeupFailureDebug.setChecked(QAIConfigController.readWakeupTestEnable(this));
        idCbWakeupFailureDebug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbWakeupFailureDebug enable onCheckedChanged: isChecked," + isChecked);
                QAIConfigController.writeWakeupTestEnable(AIConfigActivity.this, isChecked);
                if(isChecked == true){
                    idCbDumpVoice.setChecked(true);
                    QAILog.d(TAG,"idCbDumpVoiceonCheckedChanged,isChecked:",true);
                    QAIConfigController.writeEnable(AIConfigActivity.this,QAIConstants.SP_KEY_DUMP_VOICE,true);
                    // TODO TXSdkWrapper.configDump();
                    // TODO QAILog.d(TAG,"TXAIAudioSDK.dump:",TXAIAudioSDK.dump);
                }
            }
        });

        idCbEnableTXSdkLog.setChecked(QAIConfigController.readEnableWithDefault(this, QAIConstants.SHARED_PREFERENCE_KEY_ENABLE_TX_SDK_LOG, true));
        idCbEnableTXSdkLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAILog.d(TAG, "idCbEnableTXSdkLog enableTXSdkLog: isChecked," + isChecked);
                QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SHARED_PREFERENCE_KEY_ENABLE_TX_SDK_LOG, isChecked);
            }
        });

        idCbUseTestApp.setChecked(QAIConfigController.readEnableWithDefault(this, QAIConstants.SHARED_PREFERENCE_KEY_USE_TEST_SERVICE_TYPE, false));
        idCbUseTestApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SHARED_PREFERENCE_KEY_USE_TEST_SERVICE_TYPE, isChecked);
            }
        });

        if (SystemTool.isUserType()){
            idCbSaveTTSFile.setEnabled(false);
            idCbSaveTTSFile.setChecked(false);
        }else {
        idCbSaveTTSFile.setChecked(QAIConfigController.readEnableWithDefault(this, QAIConstants.SHARED_PREFERENCE_KEY_SAVE_TTS_FILE, false));
        idCbSaveTTSFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QAIConfigController.writeEnable(AIConfigActivity.this, QAIConstants.SHARED_PREFERENCE_KEY_SAVE_TTS_FILE, isChecked);
            }
        });
        }
        mIdBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idx = Integer.valueOf(idETSearchIndex.getText().toString());
                QAIConfigController.writeSearchIndex(AIConfigActivity.this, idx);
                QAILog.d(TAG, "onClick， set search index:" + idx);

                if(idCbSnsryEnable.isChecked()) {
                    int threshold = Integer.valueOf(idETSearchThreshold.getText().toString());
                    QAIConfigController.writeSnsryWakeupThreshold(AIConfigActivity.this, threshold);
                    QAILog.d(TAG, "onClick， set sny search threshold:" + threshold);
                }
                else if(idCbSpeechEnable.isChecked()){
                    float threshold = Float.valueOf(idETSearchThreshold.getText().toString());
                    QAIConfigController.writeSpeechWakeupThreshold(AIConfigActivity.this, threshold);
                    QAILog.d(TAG, "onClick， set speech search threshold:" + threshold);

                }

                finish();
            }
        });

        apiMap = new HashMap<>();
        apiMap.put(apiKeyDev, Api.AI_HOST_DEV);
        apiMap.put(apiKeyProd, Api.AI_HOST_PRODUCT);
        apiMap.put(apiKeyTest, Api.AI_HOST_TEST);

        apiList = new ArrayList<>();
        apiList.add("Choose Srv Env");
        apiList.add(apiKeyProd);//default
        apiList.add(apiKeyDev);
        apiList.add(apiKeyTest);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, apiList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        idChooseApi.setAdapter(arrayAdapter);
        idChooseApi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                if(0 == index)
                    return;

                Api.AI_HOST = apiMap.get(apiList.get(index));
                Api.restartService();

                Toast.makeText(getBaseContext(),
                        "You have selected api : " + apiList.get(index),
                        Toast.LENGTH_SHORT).show();
                if (apiList.get(index).equals(apiKeyProd)){
                    ConfigApiHelper.getInstance().changeConfig(getApplicationContext(),QAIConstants.CONFIG_ACTION_SWITCH_TO_PROD);
                } else if (apiList.get(index).equals(apiKeyDev)) {
                    ConfigApiHelper.getInstance().changeConfig(getApplicationContext(),QAIConstants.CONFIG_ACTION_SWITCH_TO_DEV);
                } else if (apiList.get(index).equals(apiKeyTest)) {
                    ConfigApiHelper.getInstance().changeConfig(getApplicationContext(),QAIConstants.CONFIG_ACTION_SWITCH_TO_TEST);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        if(idCbSnsryEnable.isChecked())
        {
            String search_threshold = String.valueOf(QAIConfigController.readSnsryWakeupThreshold(AIConfigActivity.this));
            idETSearchThreshold.setText(search_threshold);
            idETSearchIndex.setVisibility(View.VISIBLE);
            findViewById(R.id.id_et_search_index_txt).setVisibility(View.VISIBLE);
            idETSearchThreshold.setVisibility(View.VISIBLE);
            findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.VISIBLE);

        }else if(idCbSpeechEnable.isChecked()) {
            String search_threshold = String.valueOf(QAIConfigController.readSpeechWakeupThreshold(AIConfigActivity.this));
            idETSearchThreshold.setText(search_threshold);
            idETSearchIndex.setVisibility(View.INVISIBLE);
            idETSearchThreshold.setVisibility(View.VISIBLE);
            findViewById(R.id.id_et_search_index_txt).setVisibility(View.INVISIBLE);
            findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.VISIBLE);
        }else{
            idETSearchIndex.setVisibility(View.INVISIBLE);
            findViewById(R.id.id_et_search_index_txt).setVisibility(View.INVISIBLE);
            idETSearchThreshold.setVisibility(View.INVISIBLE);
            findViewById(R.id.id_et_search_threshold_txt).setVisibility(View.INVISIBLE);

        }
    }
}
