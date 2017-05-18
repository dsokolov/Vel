package me.ilich.vel.computer

import android.app.Activity
import me.ilich.vel.viper.BasePresenter
import rx.Subscription
import java.text.SimpleDateFormat
import java.util.*

class ComputerPresenter(activity: Activity) : BasePresenter(activity) {

    companion object {
        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    //    override val view: ComputerContracts.View = ComputerView(activity)
    override val interactor: ComputerContracts.Interactor = ComputerInteractor(activity)
    override val router: ComputerContracts.Router = ComputerRouter(activity)

    override fun startStopSubscriptions(): Array<Subscription> {
        //val calibratedOrientation = interactor.calibratedOrientation()
        return arrayOf(
                //                subscribePermissions(),
                //              subscribeTime(),
//                subscribePitch(calibratedOrientation),
                //subscribeAcceleration(),

/*                view.userCalibrate().
                        flatMap { interactor.uncalibratedOrientation().first() }.
                        flatMap { it -> interactor.calibrate(it) }.
                        subscribe(),*/

/*                view.userToSettings().subscribe { router.settings() },
                view.userToAbout().subscribe { router.about() },
                view.userMenu().subscribe { view.menuShow() }*/

        )
    }

/*    private fun subscribePermissions(): Subscription {
        return interactor.permissions().
                observeOn(AndroidSchedulers.mainThread()).
                subscribe {
  //                  view.configureLogger()
                    //view.updatePermissionsError(!it)
                    if (it) {
                        subscribeSpeed()
                    }
                }
    }*/

/*    private fun subscribePitch(calibratedOrientation: Observable<OrientationEntity>) =
            Observable.combineLatest(
                    interactor.pitchUnitObservable(),
                    calibratedOrientation
            ) { unit, orientation ->
                val angel = unit.value(orientation.pitch)
                val pitch = if (-1 < angel && angel < 1) {
                    "0"
                } else {
                    val unsigned = Math.abs(angel)
                    String.format("%.0f", unsigned)
                }
                val state = if (orientation.pitch > 1) {
                    AngelEntity.State.DESCEND
                } else if (orientation.pitch < -1) {
                    AngelEntity.State.ASCEND
                } else {
                    AngelEntity.State.FLAT
                }
                AngelEntity(pitch, unit, state)
            }.
                    subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe {
                        when (it.state) {
                            AngelEntity.State.ASCEND -> view.updateAngleStateAscend()
                            AngelEntity.State.DESCEND -> view.updateAngleStateDescend()
                            AngelEntity.State.FLAT -> view.updateAngleStateFlat()
                        }
                        view.updateAngelValue(it.angel)
                        view.updateAngelUnit(it.unit.titleResId)
                    }*/

/*    private fun subscribeTime(): Subscription =
            interactor.time().
                    map { SDF.format(it) }.
                    subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe { view.updateTime(it) }*/

/*    private fun subscribeSpeed() {
        addStartStopSubscription(
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
        )
    }*/

}