package me.ilich.vel.sources

import android.content.Context
import com.google.android.gms.location.LocationRequest
import me.ilich.vel.Latitude
import me.ilich.vel.Longitude
import me.ilich.vel.Speed
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.schedulers.Schedulers

data class LocationData(
        val latitude: Latitude,
        val longitude: Longitude,
        val speed: Speed
)

fun gpsObservable(context: Context): Observable<LocationData> {
    val locationProvider = ReactiveLocationProvider(context)
    val locationRequest = LocationRequest.create().
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
            setInterval(500L)
    return locationProvider.getUpdatedLocation(locationRequest).
            subscribeOn(Schedulers.io()).
            map { location ->
                LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed
                )
            }.
            subscribeOn(Schedulers.computation())
}