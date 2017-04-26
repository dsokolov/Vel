package me.ilich.vel.settings

import android.os.Bundle
import me.ilich.vel.R

class SettingsView(val activity: SettingsActivity) : SettingsContracts.View{

    override fun onCreate(savedInstanceState: Bundle?) {
        activity.setContentView(R.layout.activity_settings)
    }

    override fun onDestroy() {
        TODO("implement onDestroy")
    }

    override fun onBackPressed(onFinish: () -> Unit) {
        TODO("implement onBackPressed")
    }

}