package me.ilich.vel.computer

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class ComputerActivity : AppCompatActivity() {

    val view: ComputerContracts.View = ComputerView(this)
    val interactor: ComputerContracts.Interactor = ComputerInteractor(this)
    val router: ComputerContracts.Router = ComputerRouter(this)

    private var createSubscription: CompositeSubscription? = null
    private var startSubscription: CompositeSubscription? = null

    //override val presenter = ComputerPresenter(this)

    val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            Log.v("Sokolov", "onServiceDisconnected $name")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.v("Sokolov", "onServiceConnected $name $service")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //presenter.onBeforeCreate()
        super.onCreate(savedInstanceState)
        createSubscription?.unsubscribe()
        createSubscription = CompositeSubscription(
                interactor.themeObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            view.updateTheme(it)
                            view.onCreate(savedInstanceState)
                            view.userToSettings().subscribe { router.settings() }
                            view.userToAbout().subscribe { router.about() }
                            view.userMenu().subscribe { view.menuShow() }
                        }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        createSubscription?.unsubscribe()
        view.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        //val intent = Intent(this, VelService::class.java)
        //bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        startSubscription?.unsubscribe()
        startSubscription = CompositeSubscription(

        )
    }

    override fun onStop() {
        super.onStop()
        //unbindService(serviceConnection)
        startSubscription?.unsubscribe()
    }

    override fun onBackPressed() {
        if (view.isMenuVisible()) {
            view.menuHide()
        } else {
            super.onBackPressed()
        }
    }

}