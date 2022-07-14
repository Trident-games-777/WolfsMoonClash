package com.lilithgame.hgame.g.uploaders

import android.content.Context
import android.net.Uri
import com.lilithgame.hgame.g.loaders.Data
import com.onesignal.OneSignal

class OneSignalUploader(
    private val uri: Uri?,
    private val data: Data?,
) {
    fun upload() {
        when {
            data?.get("campaign").toString() == "null" && uri == null -> {
                OneSignal.sendTag("key2", "organic")
            }
            uri != null -> {
                OneSignal.sendTag(
                    "key2",
                    uri.toString().replace("myapp://", "").substringBefore("/")
                )
            }
            data?.get("campaign").toString() != "null" -> {
                OneSignal.sendTag(
                    "key2",
                    data?.get("campaign").toString().substringBefore("_")
                )
            }
        }
    }
}