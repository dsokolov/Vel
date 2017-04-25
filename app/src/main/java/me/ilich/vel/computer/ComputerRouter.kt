package me.ilich.vel.computer

import android.content.Context
import android.content.Intent
import me.ilich.vel.about.AboutActivity
import me.ilich.vel.settings.SettingsActivity

class ComputerRouter(val context: Context) : ComputerContracts.Router {

    override fun settings() {
        context.startActivity(Intent(context, SettingsActivity::class.java))
    }

    override fun about() {
        context.startActivity(Intent(context, AboutActivity::class.java))
    }

}