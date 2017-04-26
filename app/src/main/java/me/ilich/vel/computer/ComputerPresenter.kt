package me.ilich.vel.computer

import android.app.Activity
import me.ilich.vel.model.sources.OrientationEntity
import me.ilich.vel.viper.BasePresenter
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*

class ComputerPresenter(activity: Activity) : BasePresenter(activity) {

    companion object {
        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    override val view: ComputerContracts.View = ComputerView(activity)
    override val interactor: ComputerContracts.Interactor = ComputerInteractor(activity)
    override val router: ComputerContracts.Router = ComputerRouter(activity)

    private var startStopSubscription: CompositeSubscription? = null

    override fun startStopSubscriptions(): Array<Subscription> {
        val calibratedOrientation = interactor.calibratedOrientation()
        return arrayOf(
                subscribePermissions(),
                subscribeTime(),
                subscribeAngelValue(calibratedOrientation),
                subscribeAngelState(calibratedOrientation),
                subscribeAcceleration(),

                view.userCalibrate().
                        flatMap { interactor.uncalibratedOrientation().first() }.
                        flatMap { it -> interactor.calibrate(it) }.
                        subscribe(),

                view.userToSettings().subscribe { router.settings() },
                view.userToAbout().subscribe { router.about() }

        )
    }

    private fun subscribeAcceleration(): Subscription {
        return interactor.acceleration().
                map { acceleration ->
                    //val ac = Math.sqrt((a.x * a.x + a.y * a.y + a.z * a.z).toDouble()).toFloat()
                    //accelerationTextView.text = String.format("%.2f", ac)
                    String.format("%.2f", acceleration.y)
                }.
                subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe { view.updateAcceleration(it) }
    }

    private fun subscribePermissions(): Subscription {
        return interactor.permissions().
                observeOn(AndroidSchedulers.mainThread()).
                subscribe {
                    view.updatePermissionsError(!it)
                    if (it) {
                        subscribeSpeed()
                    }
                }
    }

    private fun subscribeAngelState(calibratedOrientation: Observable<OrientationEntity>): Subscription {
        return calibratedOrientation.observeOn(AndroidSchedulers.mainThread()).
                subscribe {
                    if (it.pitch > 1) {
                        view.updateAngleStateDescend()
                    } else if (it.pitch < -1) {
                        view.updateAngleStateAscend()
                    } else {
                        view.updateAngleStateFlat()
                    }
                }
    }

    private fun subscribeAngelValue(calibratedOrientation: Observable<OrientationEntity>): Subscription =
            calibratedOrientation.
                    map {
                        if (-1 < it.pitch && it.pitch < 1) {
                            "0"
                        } else {
                            val unsigned = Math.abs(it.pitch)
                            String.format("%.0f", unsigned)
                        }
                    }.
                    subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe { view.updateAngelValue(it) }

    private fun subscribeTime(): Subscription =
            interactor.time().
                    map { SDF.format(it) }.
                    subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe { view.updateTime(it) }

    private fun subscribeSpeed() {
        startStopSubscription?.addAll(
                interactor.location().
                        map { location ->
                            val speedKmph = location.speed / 0.36
                            String.format("%.2f", speedKmph)
                        }.
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.updateSpeed(it) }
        )
    }

}