package me.ilich.vel.model.sources

import android.content.Context
import com.google.android.gms.location.LocationRequest
import me.ilich.vel.Latitude
import me.ilich.vel.Longitude
import me.ilich.vel.MpsSpeed
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.schedulers.Schedulers

data class LocationEntity(
        val latitude: Latitude,
        val longitude: Longitude,
        val speed: MpsSpeed,
        val time: Long,
        val accuracy: Float,
        val provider: String,
        val bearing: Float
)

fun locationObservable(context: Context): Observable<LocationEntity> {
    val locationProvider = ReactiveLocationProvider(context)
    val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(100L)
    return locationProvider.getUpdatedLocation(locationRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { location ->
                LocationEntity(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed,
                        time = location.time,
                        accuracy = location.accuracy,
                        provider = location.provider,
                        bearing = location.bearing
                )
            }
}