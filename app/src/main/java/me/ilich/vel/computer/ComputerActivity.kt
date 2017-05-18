package me.ilich.vel.computer

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import me.ilich.vel.model.GpsStatus
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
    //val interactor: ComputerContracts.Interactor = ComputerInteractor(this)
    val interactor: ComputerContracts.Interactor = ComputerStubInteractor(this)
    val router: ComputerContracts.Router = ComputerRouter(this)

    private var createSubscription: CompositeSubscription? = null
    private var startSubscription: CompositeSubscription? = null
    private var speedSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interactor.onCreate(savedInstanceState)
        createSubscription?.unsubscribe()
        createSubscription = CompositeSubscription(
                interactor.themeObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            view.updateTheme(it)
                            view.onCreate(savedInstanceState)
                            view.userToSettings().subscribe { router.settings() }
                            view.userToAbout().subscribe { router.about() }
                            view.userMenu().subscribe { view.menuShow() }
                        }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        createSubscription?.unsubscribe()
        view.onDestroy()
        interactor.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        startSubscription?.unsubscribe()
        startSubscription = CompositeSubscription(
                permissionsSubscription(),
                timeSubscription(),
                batteryStatusSubscription()
        )
    }

    override fun onStop() {
        super.onStop()
        startSubscription?.unsubscribe()
        speedSubscription?.unsubscribe()
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
                            view.updateGpsStatus(GpsStatus.OK)
                            speedSubscription?.unsubscribe()
                            speedSubscription = speedSubscription()
                        } else {
                            view.updateGpsStatus(GpsStatus.NEED_PERMISSION)
                        }
                    }

    private fun speedSubscription() =
            Observable.combineLatest(
                    interactor.speedUnitObservable(),
                    interactor.location()
            ) { unit, location ->
                val speedValue = String.format("%.0f", unit.convert(location.speed))
                val speedUnit = unit.titleResId
                SpeedEntity(speedValue, speedUnit)
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        view.updateSpeedCurrent(it.speedValue)
                        view.updateSpeedUnit(it.speedUnit)
                    }

    private fun timeSubscription() =
            interactor.time()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        view.updateTime(SDF.format(it))
                    }

    private fun batteryStatusSubscription() =
            interactor.batteryStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        view.updateBatteryStatus(it)
                    }

    private data class SpeedEntity(
            val speedValue: String,
            @StringRes val speedUnit: Int
    )


}