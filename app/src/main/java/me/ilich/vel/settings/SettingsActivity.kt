package me.ilich.vel.settings

import me.ilich.vel.viper.BaseActivity

class SettingsActivity : BaseActivity() {

    override val presenter = SettingsPresenter(this)

}