package me.ilich.vel.settings

import android.support.v4.app.NavUtils

class SettingsRouter(val activity: SettingsActivity) : SettingsContract.Router {

    override fun back() {
        NavUtils.navigateUpFromSameTask(activity)
    }

}