package me.ilich.vel

import android.content.Context
import com.google.android.gms.location.LocationRequest
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.schedulers.Schedulers

sealed class LocationData {

    class Error : LocationData()

    class Values(
            val latitude: Latitude,
            val longitude: Longitude,
            val speed: Speed
    ) : LocationData()

}

fun gpsObservable(context: Context): Observable<out LocationData> {
    val locationProvider = ReactiveLocationProvider(context)
    val locationRequest = LocationRequest.create().
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
            setInterval(500L)
    return locationProvider.getUpdatedLocation(locationRequest).
            subscribeOn(Schedulers.io()).
            map { location ->
                LocationData.Values(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed
                )
            }.
            subscribeOn(Schedulers.computation())
}