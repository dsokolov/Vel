package me.ilich.vel

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import io.realm.Sort
import me.ilich.vel.model.realm.*
import me.ilich.vel.realm.RealmWrapper
import rx.Observable
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.TimeUnit

class VelService : Service() {

    companion object {
        fun intent(context: Context) = Intent(context, VelService::class.java)
    }

    private val binder = object : Binder() {

    }

    private val realm = RealmWrapper()
    private val subs = CompositeSubscription()

    override fun onCreate() {
        super.onCreate()
        realm.init().subscribe {
            subs.addAll(

                    Observable
                            .interval(1000L, TimeUnit.MILLISECONDS)
                            .map {
                                val l = Location("A")
                                l.speed = it.toFloat()
                                l
                            }
                            //locationObservable(this)
                            .flatMap { location ->
                                realm.exec { realm ->
                                    realm.executeTransaction { realm ->
                                        realm.deleteUntilCount(RealmMotion::class.java, 10000, "date", Sort.DESCENDING)
                                        val m = realm.createObject(RealmMotion::class.java)
                                        //m.gpsSpeed = location.speed
                                        val speed = Random().nextInt(100).toFloat()
                                        Log.v("Sokolov", "A speed = $speed")
                                        m.gpsSpeed = speed
                                    }
                                }
                            }
                            .subscribe(),

                    realm
                            .exec { realm ->
                                realm.where(RealmMotion::class.java)
                                        .findAllSortedAsync("date", Sort.DESCENDING)
                                        .asObservable()
                                        .map { list ->
                                            list.map { it.gpsSpeed }
                                        }
                            }
                            .flatMap { it }
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
                                Log.v("Sokolov", "D speed = $last")
                                SpeedSummary(avg, max, last)
                            }
                            .flatMap { (avg, max, last) ->
                                realm.exec { realm ->
                                    realm.executeTransaction { realm ->
                                        val s = realm.firstOrCreate(RealmSpeedSummary::class.java)
                                        s.speedMax = max
                                        s.speedAvg = avg
                                        s.speedLast = last
                                        Log.v("Sokolov", "E speed = $last")
                                    }
                                }
                            }
                            .observeOn(Schedulers.computation())
                            .subscribe()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
                .doOnNext {
                    subs.unsubscribe()
                }
                .subscribe()
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