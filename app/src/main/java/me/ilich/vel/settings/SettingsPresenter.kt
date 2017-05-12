package me.ilich.vel.settings

import android.os.Bundle
import rx.subscriptions.CompositeSubscription

class SettingsPresenter(
        val view: SettingsContract.View,
        val interactor: SettingsContract.Interactor
) {

    private val createSubscription = CompositeSubscription()

    fun onCreate(savedInstanceState: Bundle?) {
        createSubscription.add(
                interactor.theme().subscribe {
                    view.updateTheme(it)
                }
        )
    }

    fun onDestroy() {
        createSubscription.unsubscribe()
    }


}