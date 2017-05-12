package me.ilich.vel.viper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.ilich.vel.R

abstract class BaseActivity : AppCompatActivity() {

    abstract val presenter: Contracts.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onBackPressed() {
        presenter.onBackPressed {
            super.onBackPressed()
        }
    }

}