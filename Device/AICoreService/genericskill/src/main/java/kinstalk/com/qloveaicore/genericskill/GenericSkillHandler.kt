package kinstalk.com.qloveaicore.genericskill

import android.os.Handler
import android.os.HandlerThread
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.tencent.xiaowei.info.QLoveResponseInfo
import kinstalk.com.qloveaicore.ITTSCallback
import kinstalk.com.qloveaicore.genericskill.helper.CommunicatorHelper
import kinstalk.com.qloveaicore.genericskill.helper.GenericSkillUtils
import java.lang.ref.WeakReference

/**
 * Created by majorxia on 2018/5/7.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

class GenericSkillHandler : IGenericSkillHandler {
    private var mWeakCommunicator: WeakReference<CommunicatorHelper>? = null
    internal var mHT: HandlerThread
    internal var mHandler: Handler

    private val mTTSCb = object : ITTSCallback.Stub() {
        @Throws(RemoteException::class)
        override fun onTTSPlayBegin(voiceId: String) {
            Log.d(TAG, "onTTSPlayBegin: $voiceId")
        }

        @Throws(RemoteException::class)
        override fun onTTSPlayEnd(voiceId: String) {
            Log.d(TAG, "onTTSPlayEnd: $voiceId")
        }

        @Throws(RemoteException::class)
        override fun onTTSPlayProgress(voiceId: String, progress: Int) {

        }

        @Throws(RemoteException::class)
        override fun onTTSPlayError(voiceId: String, errCode: Int, errString: String) {
            Log.d(TAG, "onTTSPlayError() called with: voiceId = [" + voiceId
                    + "], errCode = [" + errCode + "], errString = [" + errString + "]")
        }
    }

    init {
        mHT = HandlerThread("GenericSkillHandler")
        mHT.start()
        mHandler = Handler(mHT.looper)
    }

    fun setWeakCommunicator(comm: CommunicatorHelper) {
        mWeakCommunicator = WeakReference(comm)
    }

    override fun handleQLoveResponseInfo(voiceId: String, qRspData: QLoveResponseInfo, extendData: ByteArray?) {
        val tts_id = GenericSkillUtils.getTTSVoiceIdFromXWRspInfo(qRspData.xwResponseInfo)
        if (!TextUtils.isEmpty(tts_id)) {
            mHandler.postDelayed({
                if (mWeakCommunicator!!.get() != null) {
                    try {
                        mWeakCommunicator!!.get()?.service?.playTextWithId(tts_id, mTTSCb)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }

                }
            }, 50)
        }
    }

    companion object {
        private val TAG = "GenericSkillHandler"
    }
}
