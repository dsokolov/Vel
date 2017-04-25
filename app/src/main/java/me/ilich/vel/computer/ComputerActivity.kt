package me.ilich.vel.computer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ComputerActivity : AppCompatActivity() {

    private val presenter = ComputerPresenter(this)

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
        val b = presenter.onBackPressed()
        if (b) {
            super.onBackPressed()
        }
    }

}