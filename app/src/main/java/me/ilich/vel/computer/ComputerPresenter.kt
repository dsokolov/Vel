package me.ilich.vel.computer

import android.app.Activity
import android.os.Bundle
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.*

class ComputerPresenter(activity: Activity) {

    companion object {
        private val SDF = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    private val view: ComputerContracts.View = ComputerView(activity)
    private val interactor: ComputerContracts.Interactor = ComputerInteractor(activity)
    private val router: ComputerContracts.Router = ComputerRouter(activity)

    private var startStopSubscription: CompositeSubscription? = null

    fun onCreate(savedInstanceState: Bundle?) {
        view.onCreate(savedInstanceState)
        interactor.onCreate(savedInstanceState)
    }

    fun onDestroy() {
        view.onDestroy()
        interactor.onDestroy()
    }

    fun onStart() {
        val calibratedOrientation = interactor.calibratedOrientation()

        startStopSubscription = CompositeSubscription(
                interactor.time().
                        map { SDF.format(it) }.
                        subscribeOn(Schedulers.computation()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.updateTime(it) },

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
                        subscribe { view.updateAngelValue(it) },

                calibratedOrientation.observeOn(AndroidSchedulers.mainThread()).
                        subscribe {
                            if (it.pitch > 1) {
                                view.updateAngleStateDescend()
                            } else if (it.pitch < -1) {
                                view.updateAngleStateAscend()
                            } else {
                                view.updateAngleStateFlat()
                            }
                        },


                interactor.permissions().
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe {
                            view.updatePermissionsError(!it)
                            if (it) {
                                subscribeSpeed()
                            }
                        },

                view.userCalibrate().
                        flatMap { interactor.uncalibratedOrientation().first() }.
                        flatMap { it -> interactor.calibrate(it) }.
                        subscribe(),

                interactor.acceleration().
                        map { acceleration ->
                            //val ac = Math.sqrt((a.x * a.x + a.y * a.y + a.z * a.z).toDouble()).toFloat()
                            //accelerationTextView.text = String.format("%.2f", ac)
                            String.format("%.2f", acceleration.y)
                        }.
                        subscribeOn(Schedulers.computation()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe { view.updateAcceleration(it) },

                view.userToSettings().subscribe { router.settings() },
                view.userToAbout().subscribe { router.about() }

        )
    }

    fun onStop() {
        startStopSubscription?.unsubscribe()
    }

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

    fun onBackPressed(): Boolean = view.onBackPressed()

}