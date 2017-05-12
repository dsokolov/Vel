package me.ilich.vel.model.sources

import android.content.Context
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.RxSharedPreferences
import me.ilich.vel.R
import me.ilich.vel.model.Theme
import rx.Observable

fun themeObservable(context: Context): Observable<Theme> {
    val sp = PreferenceManager.getDefaultSharedPreferences(context)
    val preferences = RxSharedPreferences.create(sp)
    val key = context.getString(R.string.preference_theme_key)
    val default = context.getString(R.string.preference_theme_value_default)
    val forestId = context.getString(R.string.theme_forest_id)
    val eggplantId = context.getString(R.string.theme_eggplant_id)
    val seaId = context.getString(R.string.theme_sea_id)
    val observable = preferences.getString(key, default)
            .asObservable()
            .map {
                when (it) {
                    forestId -> Theme.Forest()
                    eggplantId -> Theme.Eggplant()
                    seaId -> Theme.Sea()
                    else -> Theme.Forest()
                }
            }
    return observable
}