package kinstalk.com.common.utils;

/**
 * Created by Knight.Xu on 2017/5/25.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WaveHeader {
    public final char[] fileID = new char[]{'R', 'I', 'F', 'F'};
    public int fileLength;
    public char[] wavTag = new char[]{'W', 'A', 'V', 'E'};
    public char[] FmtHdrID = new char[]{'f', 'm', 't', ' '};
    public int FmtHdrLeth;
    public short FormatTag;
    public short Channels;
    public int SamplesPerSec;
    public int AvgBytesPerSec;
    public short BlockAlign;
    public short BitsPerSample;
    public char[] DataHdrID = new char[]{'d', 'a', 't', 'a'};
    public int DataHdrLeth;
    private ByteArrayOutputStream bos;

    public WaveHeader(int length) {
        this.fileLength = length + 36;
        this.FmtHdrLeth = 16;
        this.BitsPerSample = 16;
        this.Channels = 1;
        this.FormatTag = 1;
        this.SamplesPerSec = 16000;
        this.BlockAlign = (short) (this.Channels * this.BitsPerSample / 8);
        this.AvgBytesPerSec = this.BlockAlign * this.SamplesPerSec;
        this.DataHdrLeth = length;
    }

    public byte[] getHeader() throws IOException {
        this.bos = new ByteArrayOutputStream();
        this.WriteChar(this.bos, this.fileID);
        this.WriteInt(this.bos, this.fileLength);
        this.WriteChar(this.bos, this.wavTag);
        this.WriteChar(this.bos, this.FmtHdrID);
        this.WriteInt(this.bos, this.FmtHdrLeth);
        this.WriteShort(this.bos, this.FormatTag);
        this.WriteShort(this.bos, this.Channels);
        this.WriteInt(this.bos, this.SamplesPerSec);
        this.WriteInt(this.bos, this.AvgBytesPerSec);
        this.WriteShort(this.bos, this.BlockAlign);
        this.WriteShort(this.bos, this.BitsPerSample);
        this.WriteChar(this.bos, this.DataHdrID);
        this.WriteInt(this.bos, this.DataHdrLeth);
        this.bos.flush();
        byte[] r = this.bos.toByteArray();
        this.bos.close();
        return r;
    }

    private void WriteShort(ByteArrayOutputStream bos, int s) throws IOException {
        byte[] mybyte = new byte[]{(byte) (s << 24 >> 24), (byte) (s << 16 >> 24)};
        bos.write(mybyte);
    }

    private void WriteInt(ByteArrayOutputStream bos, int n) throws IOException {
        byte[] buf = new byte[]{(byte) (n << 24 >> 24), (byte) (n << 16 >> 24), (byte) (n << 8 >> 24), (byte) (n >> 24)};
        bos.write(buf);
    }

    private void WriteChar(ByteArrayOutputStream bos, char[] id) {
        for (int i = 0; i < id.length; ++i) {
            char c = id[i];
            bos.write(c);
        }

    }
}

