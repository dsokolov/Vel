package me.ilich.vel

import android.app.Application
import butterknife.ButterKnife
import io.realm.Realm
import io.realm.RealmConfiguration

class VelApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmCfg = RealmConfiguration.Builder().
                deleteRealmIfMigrationNeeded().
                build()
        Realm.setDefaultConfiguration(realmCfg)
        ButterKnife.setDebug(BuildConfig.DEBUG)
    }

}