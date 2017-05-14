package me.ilich.vel.computer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import me.ilich.vel.R
import me.ilich.vel.VelService
import me.ilich.vel.viper.BaseActivity

class ComputerActivity : BaseActivity() {

    override val presenter = ComputerPresenter(this)

    val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            Log.v("Sokolov", "onServiceDisconnected $name")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.v("Sokolov", "onServiceConnected $name $service")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter.onBeforeCreate()
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, VelService::class.java)
        //bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        //unbindService(serviceConnection)
    }

}