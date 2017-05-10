package me.ilich.vel.viper

import android.app.Activity
import android.os.Bundle
import rx.Subscription
import rx.subscriptions.CompositeSubscription

abstract class BasePresenter(activity: Activity) : Contracts.Presenter {

    abstract val view: Contracts.View
    abstract val interactor: Contracts.Interactor
    abstract val router: Contracts.Router

    private var startStopSubscription: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        view.onCreate(savedInstanceState)
        interactor.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        view.onDestroy()
        interactor.onDestroy()
    }

    override fun onStart() {
        val subs = startStopSubscriptions()
        startStopSubscription = CompositeSubscription(*subs)
    }

    override fun onStop() {
        startStopSubscription?.unsubscribe()
    }

    fun addStartStopSubscription(subscription: Subscription) {
        startStopSubscription?.add(subscription)
    }

    override fun onBackPressed(onFinish: () -> Unit) = view.onBackPressed(onFinish)

    protected abstract fun startStopSubscriptions(): Array<Subscription>

}

