package com.clevervpn.app

import android.app.Application
import com.clevervpn.app.ui.repository.VpnRepository
import com.clevervpn.kit.VpnClient
import com.clevervpn.kit.common.CleverVpnConfiguration

class VpnApplication : Application() {

    override fun onCreate() {
        VpnClient.init(this, CleverVpnConfiguration(10, 1200))
        VpnRepository.init(this)
        super.onCreate()
    }
}