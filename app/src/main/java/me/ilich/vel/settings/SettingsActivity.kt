package me.ilich.vel.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription


class SettingsActivity : AppCompatActivity() {

    private val view = SettingsView(this)
    private val interactor = SettingsInteractor(this)
    private val router = SettingsRouter(this)

    private val createSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSubscription.add(
                interactor.theme()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            view.updateTheme(it)
                            view.inflate(savedInstanceState)
                        }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        createSubscription.unsubscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    router.back()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(item)
                }
            }

}