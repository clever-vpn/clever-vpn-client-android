package com.clevervpn.app.ui.common

import com.clevervpn.kit.common.Status

fun Status.isStarted(): Boolean {
    return this == Status.Started
}

fun Status.isTransitioning(): Boolean {
    return this == Status.Starting || this == Status.Stopping
}

fun Status.isStopped(): Boolean {
    return this == Status.Stopped
}

fun Status.switchEnabled(): Boolean {
    return isStopped() || isStarted()
}

fun Status.switchChecked(): Boolean {
    return this == Status.Starting || this == Status.Started
}