package me.ilich.vel

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import io.realm.Sort
import me.ilich.vel.model.realm.RealmMotion
import me.ilich.vel.model.realm.RealmSpeedSummary
import me.ilich.vel.model.realm.deleteUntilCount
import me.ilich.vel.model.realm.firstOrCreate
import me.ilich.vel.model.sources.locationObservable
import me.ilich.vel.realm.RealmWrapper
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class VelService : Service() {

    companion object {
        fun intent(context: Context) = Intent(context, VelService::class.java)
    }

    private val binder = VelBinder()

    private val realm = RealmWrapper()
    private val subs = CompositeSubscription()

    override fun onCreate() {
        super.onCreate()
        realm.init().subscribe {
            subs.addAll(

                locationObservable(this)
                    .flatMap { location ->
                        realm.exec { realm ->
                            realm.executeTransaction { realm ->
                                realm.deleteUntilCount(RealmMotion::class.java, 10000, "date", Sort.DESCENDING)
                                val m = realm.createObject(RealmMotion::class.java)
                                m.gpsSpeed = location.speed
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
                        SpeedSummary(avg, max, last)
                    }
                    .flatMap { (avg, max, last) ->
                        realm.exec { realm ->
                            realm.executeTransaction { realm ->
                                val s = realm.firstOrCreate(RealmSpeedSummary::class.java)
                                s.speedMax = max
                                s.speedAvg = avg
                                s.speedLast = last
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

    class VelBinder : Binder()

}