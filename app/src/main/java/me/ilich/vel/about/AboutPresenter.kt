package me.ilich.vel.about

import me.ilich.vel.viper.BasePresenter
import me.ilich.vel.viper.Contracts
import rx.Subscription

class AboutPresenter(activity: AboutActivity) : BasePresenter(activity) {

    override val view = AboutView(activity)

    override val interactor: Contracts.Interactor
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val router: Contracts.Router
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun startStopSubscriptions(): Array<Subscription> {
        TODO("implement startStopSubscriptions")
    }

}