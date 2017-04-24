package me.ilich.vel.computer

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ComputerActivity : AppCompatActivity() {

    private val view = ComputerView(this)
    private val model = ComputerModel(this)
    private val presenter = ComputerPresenter(this, view, model)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view.onCreate(savedInstanceState)
        model.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        view.onDestroy()
        model.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

}