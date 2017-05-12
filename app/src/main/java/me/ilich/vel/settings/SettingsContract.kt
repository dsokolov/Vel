package me.ilich.vel.settings

import me.ilich.vel.model.Theme
import rx.Observable

object SettingsContract {

    interface View {
        fun updateTheme(theme: Theme)
    }

    interface Interactor {
        fun theme(): Observable<Theme>
    }

}