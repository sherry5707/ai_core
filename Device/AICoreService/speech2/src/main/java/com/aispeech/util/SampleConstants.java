package com.aispeech.util;

/**
 * @author whyme
 */
public class SampleConstants {

	public static String server,resType;

	/**
	 *  VAD资源
	 */
	public final static String RES_VAD = "vad_aihome_v0.7.bin";

	/**
	 * AEC资源设置
	 */
	public final static String RES_AEC = "AEC_ch2-2-ch1_1ref_com_20180315_v0.9.0.bin";

	/**
	 * 识别相关资源
	 */
	public final static String RES_EBNFC = "ebnfc.aicar.1.2.0.bin";
	public final static String RES_EBNFR = "ebnfr.aicar.1.2.0.bin";
	public final static String RES_GRAMMAR = "grammar.xbnf";
	public final static String local_asr_net_bin = "asr.net.bin";

	/**
	 *  唤醒资源文件
	 */
	public final static String RES_WAKEUP = "wakeup_aifar_comm_20180104.bin";


	/**
	 *  合成资源文件
	 */
	public final static String RES_TTS = "zhilingf_common_param_ce_local.v2.007.bin";
	public final static String RES_DICT = "aitts_sent_dict_v3.26.db";

	/**
	 *  服务器资源
	 */
	public final static String SERVER_PRO = "ws://s.api.aispeech.com:1028,ws://s.api.aispeech.com:80";
	public final static String SERVER_GRAY = "ws://s-test.api.aispeech.com:10000";

	public final static String RES_AIHOME = "aihome";
	public final static String RES_AIROBOT = "airobot";
	public final static String RES_AICAR = "aicar";
}
