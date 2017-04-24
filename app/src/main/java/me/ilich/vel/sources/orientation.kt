package me.ilich.vel.sources

import android.hardware.Sensor
import android.hardware.SensorManager
import com.nvanbenschoten.rxsensor.RxSensorManager
import me.ilich.vel.PitchDegree
import me.ilich.vel.RollDegree
import me.ilich.vel.YawDegree
import me.ilich.vel.radToDeg
import rx.Observable
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

data class OrientationData(
        val roll: RollDegree,
        val pitch: PitchDegree,
        val yaw: YawDegree
)

fun orientationObservable(rxSensorManager: RxSensorManager): Observable<OrientationData> {
    val uncalibratedObservable = rotationVectorOrientationObservable(rxSensorManager)
    return uncalibratedObservable
}

private fun rotationVectorOrientationObservable(rxSensorManager: RxSensorManager): Observable<OrientationData> =
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
                    OrientationData(
                            yaw = orientationVals[0].radToDeg(),
                            pitch = orientationVals[1].radToDeg(),
                            roll = orientationVals[2].radToDeg()
                    )
                }.
                subscribeOn(Schedulers.computation())