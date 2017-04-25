package me.ilich.vel.model.sources

import android.hardware.Sensor
import android.hardware.SensorManager
import com.nvanbenschoten.rxsensor.RxSensorManager
import me.ilich.vel.Acceleration
import rx.Observable
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

data class AccelerationEntity(
        val x: Acceleration,
        val y: Acceleration,
        val z: Acceleration
)

fun accelerationObservable(rxSensorManager: RxSensorManager): Observable<AccelerationEntity> =
        rxSensorManager.observeSensor(Sensor.TYPE_LINEAR_ACCELERATION, SensorManager.SENSOR_DELAY_NORMAL).
                throttleFirst(1L, TimeUnit.SECONDS).
                subscribeOn(Schedulers.io()).
                map { event ->
                    AccelerationEntity(
                            x = event.values[0],
                            y = event.values[1],
                            z = event.values[2]
                    )
                }.
                subscribeOn(Schedulers.computation())