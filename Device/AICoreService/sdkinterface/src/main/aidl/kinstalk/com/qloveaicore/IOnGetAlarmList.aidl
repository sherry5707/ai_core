// IOnGetAlarmList.aidl
package kinstalk.com.qloveaicore;

// Declare any non-default types here with import statements

interface IOnGetAlarmList {
        void onGetAlarmList(int errCode, String strVoiceID, in String[] arrayAlarmList);
}
