package com.lilithgame.hgame.g.loaders

import android.content.Context
import android.net.Uri
import com.facebook.applinks.AppLinkData
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppLinkLoader(private val context: Context) {
    suspend fun load(): Uri? = suspendCoroutine { continuation ->
        AppLinkData.fetchDeferredAppLinkData(context) { appLinkData ->
            continuation.resume(appLinkData?.targetUri)
        }
    }
}