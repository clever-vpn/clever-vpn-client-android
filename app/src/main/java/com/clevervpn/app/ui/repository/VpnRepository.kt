package com.clevervpn.app.ui.repository

import android.content.Context
import androidx.core.content.edit

object VpnRepository {
    private lateinit var appContext: Context
    private const val PREF_NAME = "pref"
    private const val KEY_PRIVACY_STATE = "privacy_state"
    private const val KEY_NOTIFICATION_PERMISSION_PROMPTED = "notification_permission_prompted"

    // 安全初始化方法
    fun init(applicationContext: Context) {
        this.appContext = applicationContext.applicationContext
    }

    fun getPrivacyState(): Boolean {
        return appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PRIVACY_STATE, false)
    }

    fun setPrivacyState(state: Boolean) {
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_PRIVACY_STATE, state)
        }
    }

    fun isNotificationPermissionPrompted(): Boolean {
        return appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_NOTIFICATION_PERMISSION_PROMPTED, false)
    }

    fun setNotificationPermissionPrompted(prompted: Boolean) {
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_NOTIFICATION_PERMISSION_PROMPTED, prompted)
        }
    }

}