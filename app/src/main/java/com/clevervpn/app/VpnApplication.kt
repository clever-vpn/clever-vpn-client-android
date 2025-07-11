package com.clevervpn.app

import android.app.Application
import android.util.Log
import com.clevervpn.app.ui.repository.VpnRepository
import com.clevervpn.kit.VpnClient
import com.wireguard.android.backend.GoBackend

class VpnApplication : Application() {

    override fun onCreate() {
        VpnClient.init(this)
        VpnRepository.init(this)
        super.onCreate()
    }
}