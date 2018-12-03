package kinstalk.com.qloveaicore.api.response;


/**
 * Created by liulinxiang on 2017/8/29.
 */

public class ListsApiRespParams {

    public String engine; //
    public String host_url; //主机名
    public int code; //成功or失败
    public boolean strict_log_uploading = false; //禁止上传log/crash
    public boolean autotest_device;
    public boolean strict_dump_audio = false; //是否需要dump 唤醒、识别的录音

    public ListsApiRespParams(String engine, String host_url, int code, boolean strict_log_uploading, boolean autotest_device, boolean strict_dump_audio) {
        this.engine = engine;
        this.host_url = host_url;
        this.code = code;
        this.strict_log_uploading = strict_log_uploading;
        this.autotest_device = autotest_device;
        this.strict_dump_audio = strict_dump_audio;
    }

    @Override
    public String toString() {
        return "ListsApiRespParams{" +
                "engine='" + engine + '\'' +
                ", host_url='" + host_url + '\'' +
                ", code=" + code +
                ", strict_log_uploading=" + strict_log_uploading +
                ", autotest_device=" + autotest_device +
                ", strict_dump_audio=" + strict_dump_audio +
                '}';
    }
}
