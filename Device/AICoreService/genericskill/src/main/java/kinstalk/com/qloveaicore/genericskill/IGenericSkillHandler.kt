package kinstalk.com.qloveaicore.genericskill

import com.tencent.xiaowei.info.QLoveResponseInfo

/**
 * Created by majorxia on 2018/5/7.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

interface IGenericSkillHandler {
    fun handleQLoveResponseInfo(voiceId: String, qRspData: QLoveResponseInfo, extendData: ByteArray?)
}
