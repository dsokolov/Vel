package me.ilich.vel.viper

import android.os.Bundle

object Contracts {

    interface View {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDestroy()
        fun onBackPressed(onFinish: () -> Unit)
    }

    interface Interactor {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDestroy()
    }

    interface Presenter {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDestroy()
        fun onStart()
        fun onStop()
        fun onBackPressed(onFinish: () -> Unit)
    }

    interface Entity

    interface Router

}