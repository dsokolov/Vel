package me.ilich.vel

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import io.realm.Realm
import io.realm.Sort
import me.ilich.vel.model.realm.*
import me.ilich.vel.model.sources.locationObservable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class VelService : Service() {

    companion object {
        fun intent(context: Context) = Intent(context, VelService::class.java)
    }

    private val binder = object : Binder() {

    }

    private lateinit var realm: Realm
    private val subs = CompositeSubscription()

    override fun onCreate() {
        super.onCreate()
        realm = Realm.getDefaultInstance()
        subs.addAll(
                locationObservable(this)
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap { location ->
                            realm.transactionObservable { realm ->
                                val m = realm.createObject(RealmMotion::class.java)
                                m.gpsSpeed = location.speed
                                realm.deleteUntilCount(RealmMotion::class.java, 10000, "date", Sort.DESCENDING)
                                //m.gpsSpeed = Random().nextInt(100).toFloat()
                            }
                        }.subscribe(),
                realm.where(RealmMotion::class.java)
                        .findAllSorted("date", Sort.DESCENDING)
                        .asObservable()
                        .map { list ->
                            list.map { it.gpsSpeed }
                        }
                        .observeOn(Schedulers.computation())
                        .map {
                            val avg = it.filter { it > 0f }
                                    .average()
                                    .let {
                                        if (it.isNaN()) {
                                            0f
                                        } else {
                                            it.toFloat()
                                        }
                                    }
                            val max = it.filter { it > 0f }
                                    .max() ?: 0f
                            val last = it.firstOrNull() ?: 0f
                            SpeedSummary(avg, max, last)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap { (avg, max, last) ->
                            realm.transactionObservable { realm ->
                                val s = realm.firstOrCreate(RealmSpeedSummary::class.java)
                                s.speedMax = max
                                s.speedAvg = avg
                                s.speedLast = last
                            }
                        }
                        .subscribe()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        subs.unsubscribe()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    data class SpeedSummary(
            val avg: MpsSpeed,
            val max: MpsSpeed,
            val last: MpsSpeed
    )

}