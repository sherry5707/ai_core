package kinstalk.com.common.utils;

import android.annotation.SuppressLint;
import android.os.Environment;

//import com.qq.wx.voice.util.Hex;
//import com.qq.wx.voice.util.Key;
//import com.qq.wx.voice.util.WaveHeader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Common2 {

    public static final String TYPE_PCM = "pcm";
    public static final String TYPE_WAV = "wav";

    /******************
     * <-- volumn --> *
     ******************/
    public static int calculateSum(byte[] buf, int len) {
        int sum = 0;
        for (int j = 0; j < len; j += 2) {
            short value = (short) (((buf[j + 1] & 0xff) << 8) | (buf[j] & 0xff));
            sum += Math.abs(value) / (len / 2);
        }

        return sum;
    }

    public static int calculateVolumn(int sum) {
        int PARAM_ENERGY_THRESHOLD_SP = 30;
        int SCALE = 100;
        double volume = 0;
        if (sum < PARAM_ENERGY_THRESHOLD_SP) {
            volume = 0;
        } else if (sum > 0x3FFF) {
            volume = SCALE;
        } else {
            volume = ((double) sum - (double) PARAM_ENERGY_THRESHOLD_SP)
                    / (12767.0 - (double) PARAM_ENERGY_THRESHOLD_SP)
                    * (double) SCALE;
        }

        return (int) volume;
    }

    public static int calculateVolumn(byte[] buf, int len) {
        int sum = calculateSum(buf, len);
        return calculateVolumn(sum);
    }

}
