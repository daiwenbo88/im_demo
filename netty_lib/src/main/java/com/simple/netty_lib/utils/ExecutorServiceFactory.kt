package com.simple.netty_lib.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author: daiwenbo
 * @date: 2021/1/30-4:07 PM
 * @description:  线程池工厂，负责重连和心跳线程调度
 */
class ExecutorServiceFactory {
    // 管理线程组，负责重连
    private var bossPool: ExecutorService?=null
    // 工作线程组，负责心跳
    private var workPool: ExecutorService?=null

    /**
     * 初始化boss线程池
     */
    @Synchronized
    fun initBossLoopGroup(){
        destroyBossLoopGroup()
        bossPool= Executors.newSingleThreadExecutor()
    }
    /**
     * 初始化work线程池
     */
    @Synchronized
    fun initWorkLoopGroup(){
        destroyWorkLoopGroup()
        workPool=Executors.newSingleThreadExecutor()
    }

    /**
     * 执行boos任务
     * @param r Runnable
     */
    fun execBossTask(r: Runnable){
        if (null==bossPool){
            initBossLoopGroup()
        }
        bossPool!!.execute(r)
    }

    /**
     * 执行work任务
     * @param r
     */
    fun execWorkTask(r: Runnable){
        if (null==workPool){
            initWorkLoopGroup()
        }
        workPool!!.execute(r)
    }

    /**
     * 释放boss线程池
     */
    @Synchronized
    fun destroyBossLoopGroup(){
        bossPool?.let {
            try {
                it.shutdown()
            }catch (t: Throwable){
                t.printStackTrace()
            }finally {
                bossPool=null
            }
        }
    }
    /**
     * 释放workPool线程池
     */
    @Synchronized
    fun destroyWorkLoopGroup(){
        workPool?.let {
            try {
                it.shutdown()
            }catch (t: Throwable){
                t.printStackTrace()
            }finally {
                workPool=null
            }
        }
    }

    /**
     * 释放所有线程池
     */
    fun destroy(){
        destroyBossLoopGroup()
        destroyWorkLoopGroup()
    }

}