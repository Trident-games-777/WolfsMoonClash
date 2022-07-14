package com.lilithgame.hgame.g.loaders

import android.content.Context
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import kotlin.coroutines.suspendCoroutine

typealias Data = MutableMap<String, Any>

class AppDataLoader(
    private val appId: String,
    private val ctx: Context
) {
    suspend fun load(): Data? = suspendCoroutine { continuation ->
        AppsFlyerLib.getInstance().init(
            appId,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    continuation.resumeWith(Result.success(data))
                }

                override fun onConversionDataFail(p0: String?) {
                    continuation.resumeWith(Result.success(null))
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
                override fun onAttributionFailure(p0: String?) {}
            },
            ctx
        )
        AppsFlyerLib.getInstance().start(ctx)
    }
}