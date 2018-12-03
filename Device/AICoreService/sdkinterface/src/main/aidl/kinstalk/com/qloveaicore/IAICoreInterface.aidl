package kinstalk.com.qloveaicore;
import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.IOnGetAlarmList;
import kinstalk.com.qloveaicore.IOnSetAlarmList;
import kinstalk.com.qloveaicore.ITTSCallback;
import kinstalk.com.qloveaicore.RequestDataResult;
import com.tencent.xiaowei.info.XWAppInfo;

interface IAICoreInterface {
    void registerService(String jsonParam, ICmdCallback cb);
    void unRegisterService(String jsonParam);
    // Depracated
    void playText(String jsonText);
    void requestData(String jsonParam);
    void getData(String jsonParam, ICmdCallback cb);
    RequestDataResult requestDataWithCb(String jsonParam, ICmdCallback cb);
    void playTextWithId(String voiceId, ITTSCallback cb);
    void playTextWithStr(String text, ITTSCallback cb);
    String textRequest(String text);
    String setFavorite(String app, String playID, boolean favorite);
    void getMusicVipInfo(ICmdCallback callback);
    String getMorePlaylist(in XWAppInfo appInfo, String playID, int maxListSize, boolean isUp, ICmdCallback cb);
    String getPlayDetailInfo(in XWAppInfo appInfo, in String[] listPlayID, ICmdCallback cb);
    String refreshPlayList(in XWAppInfo appInfo, in String[] listPlayID, ICmdCallback cb);
    int reportPlayState(in XWAppInfo appInfo, int state, String playID, String playContent, long playOffset, int playMode);
    int getDeviceAlarmList(IOnGetAlarmList listener);
    int setDeviceAlarmInfo(int opType, String strAlarmJson, IOnSetAlarmList listener);
    int updateAppState(String service, int state);
    void getLoginStatus(in XWAppInfo appInfo, ICmdCallback cb);
}
