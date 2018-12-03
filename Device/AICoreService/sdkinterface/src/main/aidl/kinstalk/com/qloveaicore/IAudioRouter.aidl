// IAudioRouter.aidl
package kinstalk.com.qloveaicore;
import kinstalk.com.qloveaicore.IAudioDataEvent;
// Declare any non-default types here with import statements

interface IAudioRouter {
        /**
         * 开始读取音频数据
         * @param onRecordDataCb 数据可读回调接口
         * @param sampleRateInHz the sample rate expressed in Hertz. 44100Hz is currently the only
         *   rate that is guaranteed to work on all devices, but other rates such as 22050,
         *   16000, and 11025 may work on some devices.
         *   {@link AudioFormat#SAMPLE_RATE_UNSPECIFIED} means to use a route-dependent value
         *   which is usually the sample rate of the source.
         *   {@link #getSampleRate()} can be used to retrieve the actual sample rate chosen.
         * @param channelConfig describes the configuration of the audio channels.
         *   See {@link AudioFormat#CHANNEL_IN_MONO} and
         *   {@link AudioFormat#CHANNEL_IN_STEREO}.  {@link AudioFormat#CHANNEL_IN_MONO} is guaranteed
         *   to work on all devices.
         * @param audioFormat the format in which the audio data is to be returned.
         *   See {@link AudioFormat#ENCODING_PCM_8BIT}, {@link AudioFormat#ENCODING_PCM_16BIT},
         *   and {@link AudioFormat#ENCODING_PCM_FLOAT}.
         */
        void startRecord(int sampleRateInHz, int channelConfig, int audioFormat, IAudioDataEvent onRecordDataCb);

        /**
         * 停止读取音频数据
         */
        void stopRecord();
        /**
         * 收到数据可读的通知后，可以调用这个接口读取录音数据
         * 该接口立刻返回
         * @param record_buffer 保存录音数据的buffer
         * @return 实际读到的数据大小，不会超过record_buffer的长度
         *         如果当前无数据可用，或者状态不对，该接口返回0
		 *
         */
        int readAudioData(inout byte[] record_buffer);

        /**
         * 向AI Service返回识别结果
         * @param jsonResult 识别结果的json格式化字符串，json格式如下
         *                   {
         *                   "resultCode":0,
         *                   "asrText":"播放"
         *                   "action":"PLAY"
         *                   }
         *
         *                   resultCode表示识别结果，定义如下：
         *                   0 - 识别成功， 1 - 识别失败， 2 - 其他识别错误(腾讯视频扩展)
         */
        void onAsrEvent(String jsonResult);
}
