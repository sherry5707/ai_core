package com.aispeech;

import android.app.Application;

import com.aispeech.common.AIConstant;

public class SpeechApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AIConstant.openLog();
//		AIConstant.setNewEchoEnable(true);// 打开AEC
//		AIConstant.setEchoCfgFile("AEC_ch2-2-ch1_1ref_common_20170710_v0.8.1.bin");// 设置AEC的配置文件
//		AIConstant.setRecChannel(2);// 默认为1,即左通道为rec录音音频,右通道为play参考音频（播放音频）若设置为2,
		// 通道会互换，即左通道为play参考音频（播放音频）,右通道为rec录音音频
		
	}

}
