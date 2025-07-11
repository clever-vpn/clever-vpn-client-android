package com.clevervpn.app.ui.repository

import android.content.Context
import androidx.core.content.edit

object VpnRepository {
    private lateinit var appContext: Context

    // 安全初始化方法
    fun init(applicationContext: Context) {
        this.appContext = applicationContext
    }

    fun getPrivacyState(): Boolean {
        return appContext.getSharedPreferences("pref", Context.MODE_PRIVATE).getBoolean("privacy_state", false)
    }

    fun setPrivacyState(state: Boolean) {
        appContext.getSharedPreferences("pref", Context.MODE_PRIVATE).edit() {
            putBoolean(
                "privacy_state",
                state
            )
        }
    }

}