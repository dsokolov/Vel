package me.ilich.vel.settings

import me.ilich.vel.viper.BasePresenter
import rx.Subscription

class SettingsPresenter(activity: SettingsActivity) : BasePresenter(activity) {

    override val view = SettingsView(activity)
    override val interactor = SettingsInteractor()
    override val router = SettingsRouter()

    override fun startStopSubscriptions(): Array<Subscription> {
        return emptyArray()
    }

}