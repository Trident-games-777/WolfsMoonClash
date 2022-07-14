package com.lilithgame.hgame.g.loaders

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerLib
import com.lilithgame.hgame.g.R
import java.util.*

class UrlLoader(
    private val base: String,
    private val uri: Uri?,
    private val data: Data?,
    private val ad: String,
    private val res: Resources,
    private val ctx: Context
) {
    fun load(): String {
        val builder = base.toUri().buildUpon()
        with(builder) {
            appendQueryParameter(
                res.getString(R.string.secure_get_parametr),
                res.getString(R.string.secure_key)
            )
            appendQueryParameter(res.getString(R.string.dev_tmz_key), TimeZone.getDefault().id)
            appendQueryParameter(res.getString(R.string.gadid_key), ad)
            appendQueryParameter(res.getString(R.string.deeplink_key), uri.toString())
            appendQueryParameter(
                res.getString(R.string.source_key),
                if (uri != null) "deeplink" else data?.get("media_source").toString()
            )
            appendQueryParameter(
                res.getString(R.string.af_id_key),
                AppsFlyerLib.getInstance().getAppsFlyerUID(ctx)
            )
            appendQueryParameter(
                res.getString(R.string.adset_id_key),
                data?.get("adset_id").toString()
            )
            appendQueryParameter(
                res.getString(R.string.campaign_id_key),
                data?.get("campaign_id").toString()
            )
            appendQueryParameter(
                res.getString(R.string.app_campaign_key),
                data?.get("campaign").toString()
            )
            appendQueryParameter(res.getString(R.string.adset_key), data?.get("adset").toString())
            appendQueryParameter(
                res.getString(R.string.adgroup_key),
                data?.get("adgroup").toString()
            )
            appendQueryParameter(
                res.getString(R.string.orig_cost_key),
                data?.get("orig_cost").toString()
            )
            appendQueryParameter(
                res.getString(R.string.af_siteid_key),
                data?.get("af_siteid").toString()
            )
        }
        return builder.toString()
    }
}