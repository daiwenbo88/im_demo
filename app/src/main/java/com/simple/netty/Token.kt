package com.simple.netty

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @author: daiwenbo
 * @date: 2021/2/1-10:30 AM
 * @description:
 */
class Token {
    //2:8.18.0:2798440883
    var mid: Long = 20529659 // im is sender id,live chat is room id
    var key: String? = null // reserved
    var roomId: String? = null // live chat protocol: soyoungLive://roomId
    var accepts = LinkedHashSet<Int>() // []int32{} business id
    var uid: Long = 20529659 // sender id
    var authorization: String? = null //sender token or room token
    var appId = 2
    var appVersion: String = "8.18.0"
    var deviceId: String = "2798440883"

    companion object {


    }

    fun toJson(): String {
        accepts.add(16012351)
        accepts.add(16012352)
        accepts.add(16012354)

        val jsonObject = JSONObject()
        jsonObject.put("mid", mid)
        jsonObject.put("key", if (TextUtils.isEmpty(key)) "" else key)
        jsonObject.put("room_id", if (TextUtils.isEmpty(roomId)) "" else roomId)
        jsonObject.put("platform", "android")
        val jsonArray = JSONArray()
        for (code in accepts) {
            jsonArray.put(code)
        }
        jsonObject.put("accepts", jsonArray)
        jsonObject.put("uid", uid)
        jsonObject.put("authorization", authorization)
        if (appId > 0) {
            jsonObject.put("app_id", appId)
        }
        if (!TextUtils.isEmpty(appVersion)) {
            jsonObject.put("app_version", appVersion)
        } else {
            jsonObject.put("app_version", "")
        }
        if (!TextUtils.isEmpty(deviceId)) {
            jsonObject.put("device_id", deviceId)
        } else {
            jsonObject.put("device_id", "")
        }
        jsonObject.put("version", 5)
        return jsonObject.toString()
    }


}