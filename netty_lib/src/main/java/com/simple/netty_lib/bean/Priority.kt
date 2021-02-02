package com.simple.netty_lib.bean


/**
 * @author: daiwenbo
 * @date: 2021/1/31-5:04 PM
 * @description: 优先级
 */
class Priority {

    companion object {
        const val LOW1: Int = 10
        const val LOW0: Int = 20
        const val NORMAL: Int = 30
        const val HIGH0: Int = 40
        const val HIGH1: Int = 50
        const val HIGH2: Int = 51


        fun priority(priority: Int): String {
            return when (priority) {
                LOW1 -> "low_1"
                LOW0 -> "low_0"
                NORMAL -> "normal"
                HIGH0 -> "high_0"
                HIGH1 -> "high_1"
                HIGH2 -> "high_2"
                else -> priority.toString()
            }
        }
    }


}