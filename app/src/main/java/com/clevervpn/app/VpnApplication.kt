package com.clevervpn.app

import android.app.Activity
import android.app.Application
import android.util.Log
import com.clevervpn.app.ui.repository.VpnRepository
import com.clevervpn.kit.VpnClient

class VpnApplication : Application() {

    companion object {
        private const val TAG = "VpnApplication"
    }

    override fun onCreate() {
        @Suppress("UNCHECKED_CAST")
        val activityClass = MainActivity::class.java as Class<Activity>
        try {
            VpnClient.init(this, activityClass)
        } catch (e: Exception) {
            Log.e(TAG, "VpnClient.init failed", e)
        }
        VpnRepository.init(this)
        super.onCreate()
    }
}