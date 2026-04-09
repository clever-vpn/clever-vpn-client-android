package com.clevervpn.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clevervpn.app.ui.common.ActivationState
import com.clevervpn.app.ui.common.switchChecked
import com.clevervpn.app.ui.repository.VpnRepository
import com.clevervpn.kit.VpnClient
import com.clevervpn.kit.common.ConnInfo
import com.clevervpn.kit.common.Line
import com.clevervpn.kit.common.LogLine
import com.clevervpn.kit.common.Protocol
import com.clevervpn.kit.common.Split
import com.clevervpn.kit.common.Traffic
import com.clevervpn.kit.common.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VpnViewModel: ViewModel() {

    private val _privacyState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _notificationPermissionPrompted: MutableStateFlow<Boolean> = MutableStateFlow(
        VpnRepository.isNotificationPermissionPrompted()
    )
    fun updatePrivacyState(state: Boolean? = null) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                if (state != null) {
                    VpnRepository.setPrivacyState(state)
                }
                _privacyState.value = VpnRepository.getPrivacyState()
            }
        }
    }


    val vpnState = VpnClient.instance.statusFlow
    val trafficState: StateFlow<Traffic?> = VpnClient.instance.trafficFlow
    val logState: StateFlow<List<LogLine>> = VpnClient.instance.logFlow
    val connInfoState: StateFlow<ConnInfo?> = VpnClient.instance.connInfoFlow
    val linesState: StateFlow<List<Line>> = VpnClient.instance.lines
    val userInfoState: StateFlow<UserInfo?> = VpnClient.instance.userInfo
    val startedAtState: StateFlow<Long?> = VpnClient.instance.startedAtFlow
    val activatedState: StateFlow<ActivationState> = userInfoState
        .map { userInfo ->
            if (userInfo?.activated == true) {
                ActivationState.ACTIVATED
            } else {
                ActivationState.DEACTIVATED
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = if (userInfoState.value?.activated == true) {
                ActivationState.ACTIVATED
            } else {
                ActivationState.DEACTIVATED
            },
        )
    val lastErrorState = VpnClient.instance.errorFlow
    val privacySate: StateFlow<Boolean> = _privacyState.asStateFlow()
    val notificationPermissionPrompted: StateFlow<Boolean> = _notificationPermissionPrompted.asStateFlow()

    fun clearLastError() {
        VpnClient.instance.clearError()
    }

    fun markNotificationPermissionPrompted() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnRepository.setNotificationPermissionPrompted(true)
                _notificationPermissionPrompted.value = true
            }
        }
    }

    fun refreshLines() {
        VpnClient.instance.refreshLines()
    }

    fun onLineSelected(lineId: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.updateLine(lineId)
            }
        }
    }

    fun subscribeTraffic() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.subscribeTraffic()
            }
        }
    }

    fun unsubscribeTraffic() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.unsubscribeTraffic()
            }
        }
    }

    fun subscribeLogs() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.subscribeLogs()
            }
        }
    }

    fun unsubscribeLogs() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.unsubscribeLogs()
            }
        }
    }

    fun updateProtocolType(protocolType: Protocol) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.updateProtocolType(protocolType)
            }
        }
    }

    fun updateSplitInfo(splitInfo: Split) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.updateSplitInfo(splitInfo)
            }
        }
    }

    fun updateRegionCode() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.updateRegionCode()
            }
        }
    }

    fun activate(key: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.activate(key)
                updateProtocolType(Protocol.AUTO)
            }
        }
    }

    fun deactivate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.deactivate()
            }
        }
    }

    fun onVpnSwitch(on: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (on) {
                    if (!vpnState.value.switchChecked()) {
                        VpnClient.instance.start()
                    }
                } else {
                    VpnClient.instance.stop()
                }
            }
        }
    }

    // fun bindActivity(activity: ComponentActivity){
    //     VpnClient.instance.bindActivity(activity)
    // }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                refreshLines()
                updatePrivacyState()
            }
        }
    }
}