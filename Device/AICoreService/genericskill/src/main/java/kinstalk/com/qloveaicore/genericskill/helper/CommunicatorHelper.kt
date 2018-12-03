package kinstalk.com.qloveaicore.genericskill.helper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.TextUtils
import android.util.Log
import com.tencent.xiaowei.info.QLoveResponseInfo
import kinstalk.com.qloveaicore.IAICoreInterface
import kinstalk.com.qloveaicore.ICmdCallback
import kinstalk.com.qloveaicore.genericskill.IGenericSkillHandler
import org.json.JSONObject
import java.util.*

/**
 * Created by majorxia on 2017/3/22.
 */

class CommunicatorHelper private constructor(internal val mContext: Context) {
    /////TODO remove belows
    var service: IAICoreInterface? = null
        private set

    private lateinit var mHandlerThread: HandlerThread
    private lateinit var mHandler: InternalHandler
    private lateinit var mTypeMap: MutableMap<String, String>
    private lateinit var mGenericHandler: IGenericSkillHandler

    private var mCb: ICmdCallback = object : ICmdCallback.Stub() {
        @Throws(RemoteException::class)
        override fun processCmd(json: String): String {
            Log.d(TAG, "processCmd: mCb, $json")
            val msg = Message.obtain()
            msg.what = MSG_HANDLE_CMD
            msg.obj = json
            mHandler.sendMessageDelayed(msg, 1)
            return ""
        }

        @Throws(RemoteException::class)
        override fun handleQLoveResponseInfo(voiceId: String, qRspData: QLoveResponseInfo?, extendData: ByteArray?) {
            Log.d(TAG, "handleQLoveResponseInfo() called with: voiceId = [" + voiceId + "], rspData = [" + qRspData
                    + "], extendData = [" + extendData + "]")

            if (qRspData != null && qRspData.xwResponseInfo != null && qRspData.xwResponseInfo.appInfo != null) {
                if (TextUtils.equals(type_generic, qRspData.qServiceType)) {
                    mGenericHandler.handleQLoveResponseInfo(voiceId, qRspData, extendData)
                }
            }
        }

        @Throws(RemoteException::class)
        override fun handleWakeupEvent(i: Int, s: String) {
        }
    }

    private val sc = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected: Enter")
            this@CommunicatorHelper.service = IAICoreInterface.Stub.asInterface(service)
            registerClient()
            Log.d(TAG, "onServiceConnected: get service" + this@CommunicatorHelper.service!!)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: Enter")
            service = null

            rebindRemoteService(3000)
        }
    }

    private val serviceIntent: Intent
        get() {
            val cn = ComponentName(remoteSvcPkg, remoteSvcCls)

            val i = Intent()
            i.component = cn
            return i
        }

    fun setGenericSkillHandler(genericSkillHandler: IGenericSkillHandler) {
        mGenericHandler = genericSkillHandler
    }

    fun init() {
        Log.d(TAG, "init: Enter")
        mTypeMap = HashMap()

        mTypeMap[type_generic] = "com.example.android.mediaplayersample.GenericActivity"

        mContext.bindService(serviceIntent, sc, Context.BIND_AUTO_CREATE)
        mHandlerThread = HandlerThread("slh_handler_thread")
        mHandlerThread.start()
        mHandler = InternalHandler(mHandlerThread.looper)
    }

    fun unInit() {
        if (service != null) {
            try {
                service!!.unRegisterService(GenericSkillUtils.buildJson(type_generic,
                        localSvcPkg,
                        localSvcCls))
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }
        service = null
        mContext.unbindService(sc)
        mHandler.removeCallbacksAndMessages(null)
        mHandlerThread.quit()
    }

    fun registerClient() {
        Log.d(TAG, "registerClient: ")
        try {
            for (type in mTypeMap.keys) {
                service!!.registerService(GenericSkillUtils.buildJson(type,
                        localSvcPkg,
                        localSvcCls), mCb)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun getData(jsonParam: String) {
        try {
            service!!.getData(jsonParam, mCb)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    private inner class InternalHandler internal constructor(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RECONNECT_REMOTE -> {
                    Log.d(TAG, "handleMessage: MSG_RECONNECT_REMOTE")
                    mContext.bindService(serviceIntent, sc, Context.BIND_AUTO_CREATE)
                }
                MSG_HANDLE_CMD -> try {
                    val c = msg.obj as String
                    val json = JSONObject(c)
                    val text = json.optJSONObject("answer").optString("text")
                    val speed = 1
                    val role = 2
                    service!!.playText(GenericSkillUtils.buildPlayTextJson(text, speed, role))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                else -> {
                }
            }
        }
    }

    private fun rebindRemoteService(milliSeconds: Int) {
        Log.d(TAG, "rebindRemoteService: Enter,$milliSeconds")
        service = null

        // re-bind to the service if disconnected
        val m = Message.obtain()
        m.what = MSG_RECONNECT_REMOTE
        mHandler.sendMessageDelayed(m, milliSeconds.toLong())
    }

    fun registerClient(jsonParam: String, cb: ICmdCallback) {
        try {
            service!!.registerService(jsonParam, cb)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun setFavorite(app: String, playID: String, favorite: Boolean): String {
        try {
            return service!!.setFavorite(app, playID, favorite)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return ""
    }

    fun getMusicVipInfo() {
        try {
            service!!.getMusicVipInfo(object : ICmdCallback.Stub() {
                @Throws(RemoteException::class)
                override fun processCmd(s: String): String? {
                    return null
                }

                @Throws(RemoteException::class)
                override fun handleQLoveResponseInfo(s: String, qLoveResponseInfo: QLoveResponseInfo, bytes: ByteArray) {
                    Log.d(TAG, "getMusicVipInfo : handleQLoveResponseInfo() called with: s = ["
                            + s + "], qLoveResponseInfo = [" + qLoveResponseInfo + "], bytes = [" + bytes + "]")
                }

                @Throws(RemoteException::class)
                override fun handleWakeupEvent(i: Int, s: String) {

                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun getDeviceAlarmList() {
    }

    fun setCmdHandler(cb: ICmdCallback) {
        mCb = cb
    }

    companion object {

        private val TAG = "AI-CommunicatorHelper"
        private var sInstance: CommunicatorHelper? = null

        val type_generic = "generic"

        val remoteSvcPkg = "kinstalk.com.qloveaicore"
        val remoteSvcCls = "kinstalk.com.qloveaicore.QAICoreService"
        val localSvcPkg = "kinstalk.com.qloveaicore.genericskill"
        val localSvcCls = "kinstalk.com.qloveaicore.genericskill.GenericSkillService"

        private val MSG_RECONNECT_REMOTE = 0x1
        private val MSG_HANDLE_CMD = 0x2

        @Synchronized
        fun getInstance(c: Context): CommunicatorHelper {
            if (sInstance == null) {
                sInstance = CommunicatorHelper(c.applicationContext)
            }
            return sInstance as CommunicatorHelper
        }
    }

}
