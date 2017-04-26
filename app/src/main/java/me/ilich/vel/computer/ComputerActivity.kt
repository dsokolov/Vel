package me.ilich.vel.computer

import me.ilich.vel.viper.BaseActivity

class ComputerActivity : BaseActivity() {

    override val presenter = ComputerPresenter(this)

}