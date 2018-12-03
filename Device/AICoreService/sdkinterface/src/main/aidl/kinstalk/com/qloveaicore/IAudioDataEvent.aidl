// IAudioDataEvent.aidl
package kinstalk.com.qloveaicore;

// Declare any non-default types here with import statements

interface IAudioDataEvent {
        /**
         * 家圆AI Service通知第三方应用数据可读的通知接口
         *
         * @param event 事件值。具体定义如下：
         *              1 - 数据可读
         * @param jsonParam 事件参数，用于以后扩展，暂时不用
         */
        void onAudioDataEvent(int event, String jsonParam);
}
