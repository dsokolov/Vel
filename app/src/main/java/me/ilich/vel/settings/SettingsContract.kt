package me.ilich.vel.settings

import android.os.Bundle
import me.ilich.vel.model.Theme
import rx.Observable

object SettingsContract {

    interface View {
        fun inflate(savedInstanceState: Bundle?)
        fun updateTheme(theme: Theme)
    }

    interface Interactor {
        fun theme(): Observable<Theme>
    }

    interface Router {
        fun back()
    }

}