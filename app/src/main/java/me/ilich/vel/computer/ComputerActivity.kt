package me.ilich.vel.computer

import android.app.Service
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import me.ilich.vel.VelService
import me.ilich.vel.realm.RealmSchedulers
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*

class ComputerActivity : AppCompatActivity() {

    companion object {
        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    val view: ComputerContracts.View = ComputerView(this)
    val interactor: ComputerContracts.Interactor = ComputerInteractor(this)
    //val interactor: ComputerContracts.Interactor = ComputerStubInteractor(this)
    val router: ComputerContracts.Router = ComputerRouter(this)

    private var viewInflated = false
    private var started = false

    private var createSubscription: CompositeSubscription? = null
    private var startSubscription: CompositeSubscription? = null
    private var speedUnitSubscription: Subscription? = null
    private var currentSpeedSubscription: Subscription? = null
    private var avgSpeedSubscription: Subscription? = null
    private var maxSpeedSubscription: Subscription? = null

    private var binder: VelService.VelBinder? = null

    private var serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            binder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as VelService.VelBinder
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interactor.onCreate(savedInstanceState)
        createSubscription?.unsubscribe()
        createSubscription = CompositeSubscription(
            themeSubscription(savedInstanceState)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        createSubscription?.unsubscribe()
        view.onDestroy()
        interactor.onDestroy()
    }

    private fun themeSubscription(savedInstanceState: Bundle?): Subscription? {
        return interactor.themeObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.updateTheme(it)
                view.onCreate(savedInstanceState)
                view.userToSettings().subscribe {
                    router.settings()
                    view.menuHide()
                }
                view.userToAbout().subscribe {
                    router.about()
                    view.menuHide()
                }
                view.userResetSpeed()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { view.menuHide() }
                    .doOnNext { view.showDialogSpeedReset() }
                    .flatMap { view.resetSpeedDialogObservable }
                    .filter { it }
                    .flatMap { interactor.speedReset() }
                    .subscribe()
                view.userMenu().subscribe { view.menuShow() }
                viewInflated = true
                subscribeStart()
            }
    }

    override fun onStart() {
        super.onStart()
        started = true
        subscribeStart()
    }

    private fun subscribeStart() {
        if (started && viewInflated) {
            startSubscription?.unsubscribe()
            startSubscription = CompositeSubscription(
                permissionsSubscription(),
                timeSubscription()//,
//                    batteryStatusSubscription()
            )
        }
    }

    override fun onStop() {
        super.onStop()
        if (binder != null) {
            unbindService(serviceConnection)
            binder = null
        }
        startSubscription?.unsubscribe()
        speedUnitSubscription?.unsubscribe()
        Observable.just(Unit)
            .subscribeOn(RealmSchedulers.getInstance())
            .doOnNext {
                currentSpeedSubscription?.unsubscribe()
                maxSpeedSubscription?.unsubscribe()
                avgSpeedSubscription?.unsubscribe()
            }
            .subscribe()
    }

    override fun onBackPressed() {
        if (view.isMenuVisible()) {
            view.menuHide()
        } else {
            super.onBackPressed()
        }
    }

    private fun permissionsSubscription() =
        interactor.permissions().
            observeOn(AndroidSchedulers.mainThread()).
            subscribe {
                if (it) {
                    //view.updateGpsStatus(GpsStatus.OK)
                    speedUnitSubscription?.unsubscribe()
                    speedUnitSubscription = speedUnitSubscription()
                    currentSpeedSubscription?.unsubscribe()
                    currentSpeedSubscription = currentSpeedSubscription()
                    maxSpeedSubscription?.unsubscribe()
                    maxSpeedSubscription = maxSpeedSubscription()
                    avgSpeedSubscription?.unsubscribe()
                    avgSpeedSubscription = avgSpeedSubscription()
                    bindService(VelService.intent(this), serviceConnection, Service.BIND_AUTO_CREATE)
                } else {
                    //view.updateGpsStatus(GpsStatus.NEED_PERMISSION)
                }
            }

    private fun speedUnitSubscription() = interactor.speedUnitObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.updateSpeedUnit(it.titleResId) }

    private fun currentSpeedSubscription() =
        Observable.combineLatest(
            interactor.speedUnitObservable(),
            interactor.speedCurrent()
        ) { unit, speed ->
            unit.convert(speed)
        }
            .map { String.format("%.1f", it) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.updateSpeedCurrent(it) }

    private fun maxSpeedSubscription() =
        Observable.combineLatest(
            interactor.speedUnitObservable(),
            interactor.speedMax()
        ) { unit, speed ->
            unit.convert(speed)
        }
            .map { String.format("%.1f", it) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.updateSpeedMax(it) }

    private fun avgSpeedSubscription() =
        Observable.combineLatest(
            interactor.speedUnitObservable(),
            interactor.speedAvg()
        ) { unit, speed ->
            unit.convert(speed)
        }
            .map { String.format("%.1f", it) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.updateSpeedAvg(it) }

    private fun timeSubscription() =
        interactor.time()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.updateTime(SDF.format(it))
            }

/*    private fun batteryStatusSubscription() =
            interactor.batteryStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        view.updateBatteryStatus(it)
                    }*/

/*    private data class SpeedEntity(
            val speedValue: String,
            @StringRes val speedUnit: Int
    )*/

}