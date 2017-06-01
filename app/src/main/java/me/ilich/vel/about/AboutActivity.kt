package me.ilich.vel.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding.view.clicks
import me.ilich.vel.ActivityColor
import me.ilich.vel.BuildConfig
import me.ilich.vel.R
import me.ilich.vel.model.sources.themeObservable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers


class AboutActivity : AppCompatActivity() {

    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.version) lateinit var version: TextView
    @BindView(R.id.rate) lateinit var rate: Button

    val activityColor = ActivityColor(this)
    var rateSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeObservable(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { theme ->
                    activityColor.changeTheme(theme)
                    setContentView(R.layout.activity_about)
                    ButterKnife.bind(this)
                    setSupportActionBar(toolbar)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    activityColor.created()
                    version.text = getString(R.string.about_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
                    rateSubscription = rate.clicks().subscribe {
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)))
                        } catch (e: ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)))
                        }
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        rateSubscription?.unsubscribe()
    }

}