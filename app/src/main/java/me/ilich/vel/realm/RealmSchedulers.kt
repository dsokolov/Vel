package me.ilich.vel.realm

import android.os.HandlerThread
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers

object RealmSchedulers {

    private val handlerThread: HandlerThread = HandlerThread("RxRealmThread")

    fun getInstance(): Scheduler {
        if (!handlerThread.isAlive) {
            handlerThread.start()
        }
        return AndroidSchedulers.from(handlerThread.looper)
    }

}