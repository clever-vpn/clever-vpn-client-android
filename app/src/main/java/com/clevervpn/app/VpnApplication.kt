package com.clevervpn.app

import android.app.Application
import com.clevervpn.app.ui.repository.VpnRepository
import com.clevervpn.kit.VpnClient

class VpnApplication : Application() {

    override fun onCreate() {
        VpnClient.init(this)
        VpnRepository.init(this)
        super.onCreate()
    }
}