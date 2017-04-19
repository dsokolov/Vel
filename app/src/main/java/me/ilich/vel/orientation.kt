package me.ilich.vel

import android.hardware.Sensor
import android.hardware.SensorManager
import com.nvanbenschoten.rxsensor.RxSensorManager
import rx.Observable
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

sealed class OrientationData {

    class NoSensor : OrientationData()

    data class Values(
            val roll: RollDegree,
            val pitch: PitchDegree,
            val yaw: YawDegree
    ) : OrientationData()

}

fun orientationObservable(sensorManager: SensorManager, rxSensorManager: RxSensorManager): Observable<out OrientationData> {
    val hasRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null
    val uncalibratedObservable = if (hasRotationVector) {
        rotationVectorOrientationObservable(rxSensorManager)
    } else {
        Observable.interval(1L, TimeUnit.SECONDS).
                map { OrientationData.NoSensor() }.
                subscribeOn(Schedulers.computation())
    }
    return uncalibratedObservable
}

private fun rotationVectorOrientationObservable(rxSensorManager: RxSensorManager): Observable<out OrientationData> =
        rxSensorManager.observeSensor(Sensor.TYPE_ROTATION_VECTOR, SensorManager.SENSOR_DELAY_NORMAL).
                throttleFirst(200L, TimeUnit.MILLISECONDS).
                subscribeOn(Schedulers.io()).
                map {
                    event ->
                    val orientationVals = FloatArray(3)
                    val rotationMatrix = FloatArray(9)
                    val adjustedRotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, adjustedRotationMatrix)
                    SensorManager.getOrientation(adjustedRotationMatrix, orientationVals)
                    OrientationData.Values(
                            yaw = orientationVals[0].radToDeg(),
                            pitch = orientationVals[1].radToDeg(),
                            roll = orientationVals[2].radToDeg()
                    )
                }.
                subscribeOn(Schedulers.computation())