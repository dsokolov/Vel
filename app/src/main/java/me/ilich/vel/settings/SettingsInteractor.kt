package me.ilich.vel.settings

import android.content.Context
import me.ilich.vel.model.Theme
import me.ilich.vel.model.sources.themeObservable
import rx.Observable

class SettingsInteractor(val context: Context) : SettingsContract.Interactor {

    override fun theme(): Observable<Theme> = themeObservable(context)

}