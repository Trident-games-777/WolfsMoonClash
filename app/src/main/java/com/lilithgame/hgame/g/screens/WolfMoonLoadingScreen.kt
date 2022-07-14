package com.lilithgame.hgame.g.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lilithgame.hgame.g.databinding.ScreenWolfMoonLoadingBinding
import com.lilithgame.hgame.g.loaders.AppDataLoader
import com.lilithgame.hgame.g.loaders.AppLinkLoader
import com.lilithgame.hgame.g.loaders.UrlLoader
import com.lilithgame.hgame.g.repository.WolfMoonRepository
import com.lilithgame.hgame.g.screens.WolfMoonSplashScreen.Companion.EXTRA_AD_ID
import com.lilithgame.hgame.g.screens.WolfMoonSplashScreen.Companion.EXTRA_APPS_ID
import com.lilithgame.hgame.g.screens.WolfMoonSplashScreen.Companion.EXTRA_BASE_LINK_DATA
import com.lilithgame.hgame.g.screens.WolfMoonSplashScreen.Companion.EXTRA_LINK_DATA
import com.lilithgame.hgame.g.uploaders.OneSignalUploader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WolfMoonLoadingScreen : AppCompatActivity() {
    private val binding by lazy {
        ScreenWolfMoonLoadingBinding.inflate(layoutInflater)
    }
    private val base by lazy { requireNotNull(intent.getStringExtra(EXTRA_BASE_LINK_DATA)) }
    private val appsID by lazy { requireNotNull(intent.getStringExtra(EXTRA_APPS_ID)) }
    private val adID by lazy { requireNotNull(intent.getStringExtra(EXTRA_AD_ID)) }
    private val repo by lazy {
        WolfMoonRepository(
            getSharedPreferences(
                "prefs",
                Context.MODE_PRIVATE
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        when (repo.getIsSaved()) {
            true -> {
                val url = repo.getUrl()
                startWeb(url, base)
            }
            false -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val uri = AppLinkLoader(this@WolfMoonLoadingScreen).load()
                    val url = uri?.let {
                        UrlLoader(
                            base = base,
                            uri = it,
                            data = null,
                            ad = adID,
                            res = resources,
                            ctx = this@WolfMoonLoadingScreen
                        ).load().also { OneSignalUploader(uri = uri, data = null).upload() }
                    } ?: run {
                        val appData = AppDataLoader(appsID, this@WolfMoonLoadingScreen).load()
                        UrlLoader(
                            base = base,
                            uri = null,
                            data = appData,
                            ad = adID,
                            res = resources,
                            ctx = this@WolfMoonLoadingScreen
                        ).load().also { OneSignalUploader(uri = null, data = appData).upload() }
                    }
                    startWeb(url, base)
                }
            }
        }
    }

    private fun startWeb(str: String, baseStr: String) {
        val intent = Intent(this@WolfMoonLoadingScreen, WolfMoonWebScreen::class.java)
        intent.putExtra(EXTRA_LINK_DATA, str)
        intent.putExtra(EXTRA_BASE_LINK_DATA, baseStr)
        startActivity(intent)
        finish()
    }
}