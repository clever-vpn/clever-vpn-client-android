package com.clevervpn.app.ui.common

import com.clevervpn.kit.common.VpnState

fun VpnState.switchEnabled(): Boolean {
    return when (this) {
        is VpnState.Up,
        VpnState.Down -> true
        else -> false
    }
}

fun VpnState.switchChecked(): Boolean {
    return when (this) {
        is VpnState.Up -> true
        VpnState.Reconnecting -> true
        else -> false
    }
}