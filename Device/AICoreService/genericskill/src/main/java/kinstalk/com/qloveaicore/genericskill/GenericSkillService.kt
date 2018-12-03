package kinstalk.com.qloveaicore.genericskill

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

import kinstalk.com.qloveaicore.ITTSCallback
import kinstalk.com.qloveaicore.genericskill.helper.CommunicatorHelper
import kinstalk.com.qloveaicore.genericskill.helper.GenericSkillUtils

class GenericSkillService : Service() {

    private lateinit var mCommunicatorHelper: CommunicatorHelper
    private lateinit var mHandler: Handler
    private lateinit var mGenericHandler: GenericSkillHandler

    private val mTTSCb = object : ITTSCallback.Stub() {
        @Throws(RemoteException::class)
        override fun onTTSPlayBegin(voiceId: String) {
            Log.d(TAG, "onTTSPlayBegin() called with: voiceId = [$voiceId]")
        }

        @Throws(RemoteException::class)
        override fun onTTSPlayEnd(voiceId: String) {
        }

        @Throws(RemoteException::class)
        override fun onTTSPlayProgress(s: String, i: Int) {
        }

        @Throws(RemoteException::class)
        override fun onTTSPlayError(s: String, i: Int, s1: String) {
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: Enter")

        super.onCreate()
        mGenericHandler = GenericSkillHandler()

        mCommunicatorHelper = CommunicatorHelper.getInstance(applicationContext)
        mCommunicatorHelper.setGenericSkillHandler(mGenericHandler)
        mGenericHandler.setWeakCommunicator(mCommunicatorHelper) // hold each other

        mCommunicatorHelper.init()
        mHandler = Handler(mainLooper)
    }

    init {
        Log.d(TAG, "AI-GenericSkillService constructor: Enter")
    }

    fun playText(jsonText: String) {
        try {
            mCommunicatorHelper.service!!.playText(jsonText)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun textRequest(text: String) {
        try {
            mCommunicatorHelper.service?.textRequest(text)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun playTextWithId(voiceId: String) {
        try {
            mCommunicatorHelper.service!!.playTextWithId(voiceId, mTTSCb)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun execCommand(command: String) {
        val delimiter = ":="
        val subCommand = command.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (subCommand.size >= 1) {
            val action = subCommand[0]
            var arg1 = ""
            var arg2 = ""

            when (action) {
                CMD_PLAY_TEXT -> {
                    arg1 = subCommand[1]

                    try {
                        val playText = GenericSkillUtils.buildPlayTextJson(arg1, 0, 0)
                        mCommunicatorHelper.service?.playText(playText)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }

                }
                CMD_UPDATE_APP_STATE -> try {
                    arg1 = subCommand[1]
                    arg2 = subCommand[2]
                    mCommunicatorHelper.service!!.updateAppState(arg1, Integer.valueOf(arg2))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

                else -> Log.i(TAG, "execCommand: no command handler")
            }
        }

    }

    fun setFavorite(app: String, playID: String, favorite: Boolean): String {
        try {
            return mCommunicatorHelper.service!!.setFavorite(app, playID, favorite)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return ""
    }

    fun getDeviceAlarmList() {
        mCommunicatorHelper.getDeviceAlarmList()
    }

    fun getMusicVipInfo() {
        mCommunicatorHelper.getMusicVipInfo()
    }

    fun getData(data: String) {
        mCommunicatorHelper.getData(data)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind: Enter")

        return QBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: Enter")
        return Service.START_STICKY
    }

    override fun onLowMemory() {
        Log.d(TAG, "onLowMemory: Enter")
        super.onLowMemory()
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind: Enter")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind: Enter")
        super.onRebind(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "onConfigurationChanged: Enter")
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Enter")
        mCommunicatorHelper.unInit()
        super.onDestroy()
    }

    inner class QBinder : Binder() {
        val service: GenericSkillService
            get() = this@GenericSkillService
    }

    companion object {
        private val TAG = "AI-GenericSkillService"

        val CMD_PLAY_TEXT = "a"
        val CMD_UPDATE_APP_STATE = "b"
    }
}
