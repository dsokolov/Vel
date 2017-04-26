package me.ilich.vel.about

import android.os.Bundle
import android.support.v7.widget.Toolbar
import me.ilich.vel.R

class AboutView(val activity: AboutActivity) : AboutContracts.View {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        activity.setContentView(R.layout.activity_about)
        toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        activity.setSupportActionBar(toolbar)
    }

    override fun onDestroy() {

    }

    override fun onBackPressed(onFinish: () -> Unit) {
        onFinish()
    }

}