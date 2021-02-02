package com.simple.netty_lib.bean

/**
 * @author: daiwenbo
 * @date: 2021/1/31-5:16 PM
 * @description: goim 通信标识码
 */
class OpCode{
    companion object{
        val OP_HAND_SHAKE = 0 // handshake

        val OP_HAND_SHAKE_REPLY = 1 // handshake reply

        val OP_HEART_BEAT = 2 // heartbeat

        val OP_HEART_BEAT_REPLY = 3 // heartbeat reply

        val OP_SEND_MSG = 4 // send message

        val OP_SEND_MSG_REPLY = 5 // send message reply

        val OP_DISCONNECT_REPLY = 6 // connection disconnect reply

        val OP_AUTH = 7 // auth connnect

        val OP_AUTH_REPLY = 8 // auth connect reply

        val OP_RAW_BATCH = 9 // batch message for websocket

        val OP_AUTH_SSO_REPLY = 19
        val OP_MSG_RECEIPT = 20
        val OP_MSG_RECEIPT_REPLY = 21
        val OP_MAX_END = 99

        fun op(op: Int): String? {
            return when (op) {
                OP_HAND_SHAKE -> "hand_shake"
                OP_HAND_SHAKE_REPLY -> "hand_shake_reply"
                OP_HEART_BEAT -> "heart_beat"
                OP_HEART_BEAT_REPLY -> "heart_beat_reply"
                OP_SEND_MSG -> "send_msg"
                OP_SEND_MSG_REPLY -> "send_msg_reply"
                OP_DISCONNECT_REPLY -> "disconnect_reply"
                OP_AUTH -> "auth"
                OP_AUTH_REPLY -> "auth_reply"
                OP_RAW_BATCH -> "raw_batch"
                OP_AUTH_SSO_REPLY -> "auth_sso_reply"
                OP_MSG_RECEIPT -> "msg_receipt"
                OP_MSG_RECEIPT_REPLY -> "msg_receipt_reply"
                else -> op.toString()
            }
        }
    }


}