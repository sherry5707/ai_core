package kinstalk.com.qloveaicore.genericskill.helper

import com.tencent.xiaowei.info.XWResponseInfo
import org.json.JSONObject

/**
 * Created by majorxia on 2018/5/7.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

object GenericSkillUtils {
    val AI_JSON_FIELD_TYPE = "type"
    val AI_JSON_FIELD_PACKAGE = "pkg"
    val AI_JSON_FIELD_SERVICECLASS = "svcClass"
    val AI_JSON_PLAYTEXT_TEXT = "text"
    val AI_JSON_PLAYTEXT_SPEED = "speed"
    val AI_JSON_PLAYTEXT_ROLE = "role"

    fun buildPlayTextJson(text: String, speed: Int, role: Int): String {
        val json = JSONObject()
        try {
            json.put(AI_JSON_PLAYTEXT_TEXT, text)
            json.put(AI_JSON_PLAYTEXT_SPEED, speed)
            json.put(AI_JSON_PLAYTEXT_ROLE, role)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return json.toString()
    }

    fun buildJson(type: String, pkg: String, svc: String): String {
        val json = JSONObject()
        try {
            json.put(AI_JSON_FIELD_TYPE, type)
            json.put(AI_JSON_FIELD_PACKAGE, pkg)
            json.put(AI_JSON_FIELD_SERVICECLASS, svc)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return json.toString()
    }

    fun getTTSVoiceIdFromXWRspInfo(respInfo: XWResponseInfo?): String {
        var tts_voice_id = ""

        try {
            if (respInfo != null && respInfo.resources != null) {
                for (resGroup in respInfo.resources) {
                    for (resource in resGroup.resources) {
                        if (resource.format == 2) {
                            tts_voice_id = resource.ID
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tts_voice_id
    }

}
