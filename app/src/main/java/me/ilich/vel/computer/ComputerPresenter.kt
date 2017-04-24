package me.ilich.vel.computer

import android.app.Activity
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class ComputerPresenter(
        val activity: Activity,
        val view: ComputerView,
        val model: ComputerModel
) {

    private val startStopSubscription = CompositeSubscription()

    fun onStart() {
        startStopSubscription.addAll(
                model.timeObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.showTime(it) },

                model.calibratedOrientationObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.showAngel(it) },

                model.permissionsObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe {
                            if (it) {
                                view.hideError()
                                subscribeSpeed()
                            } else {
                                view.showError("GPS disabled")
                            }
                        },

                view.calibrationObservable().
                        flatMap {
                            model.uncalibratedOrientationObservable().first()
                        }.
                        flatMap { orientation ->
                            model.calibrate(orientation)
                        }.
                        subscribe(),

                model.accelerationObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.showAcceleration(it) }
        )
    }

    fun onStop() {
        startStopSubscription.unsubscribe()
    }

    private fun subscribeSpeed() {
        startStopSubscription.addAll(
                model.locationObservable().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.showSpeed(it.speed) }
        )
    }

}