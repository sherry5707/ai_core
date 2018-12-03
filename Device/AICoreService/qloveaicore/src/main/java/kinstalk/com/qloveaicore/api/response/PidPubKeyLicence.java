package kinstalk.com.qloveaicore.api.response;

import java.io.Serializable;

/**
 * Created by Knight.Xu on 2017/10/13.
 */

public class PidPubKeyLicence implements Serializable {
    /**
     * engine : advtech
     * code : 0
     * data : {"pid":"2100000151","license":"3045022034B52B7BC77BDA5B038ADBFD10E382C04E612E4FDC67023C8238B66DAA0DC5F7022100FE2BB9FB2CD981517D4D89044C9A647228387261F46596E2320B6D1D40B0256F","pub":"0460DCAFB7C97FE71B9F6D39436A247C5B15EAC4A4494052A6F20647EF42A2A3EABB4EA8DC6BF341929A847E42C5FBEFBF"}
     */

    private String engine;
    private int code;
    private DataBean data;

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pid : 2100000151
         * license : 3045022034B52B7BC77BDA5B038ADBFD10E382C04E612E4FDC67023C8238B66DAA0DC5F7022100FE2BB9FB2CD981517D4D89044C9A647228387261F46596E2320B6D1D40B0256F
         * pub : 0460DCAFB7C97FE71B9F6D39436A247C5B15EAC4A4494052A6F20647EF42A2A3EABB4EA8DC6BF341929A847E42C5FBEFBF
         */

        private String pid;
        private String license;
        private String pub;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getPub() {
            return pub;
        }

        public void setPub(String pub) {
            this.pub = pub;
        }
    }
}
