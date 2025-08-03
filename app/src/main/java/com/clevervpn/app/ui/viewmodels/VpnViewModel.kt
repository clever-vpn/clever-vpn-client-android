package com.clevervpn.app.ui.viewmodels

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clevervpn.app.ui.common.ActivationState
import com.clevervpn.app.ui.repository.VpnRepository
import com.clevervpn.kit.VpnClient
import com.clevervpn.kit.common.Location
import com.clevervpn.kit.common.LogLine
import com.clevervpn.kit.common.ProtocolType
import com.clevervpn.kit.common.Traffic
import com.clevervpn.kit.common.UserInfo
import com.clevervpn.kit.common.VpnState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VpnViewModel: ViewModel() {

    private val _userInfoState: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    private val _activatedState: MutableStateFlow<ActivationState> = MutableStateFlow(ActivationState.LOCAL_CHECK)
    private fun updateUserInfoState() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _userInfoState.value = VpnClient.instance.getUserInfo()
                _activatedState.value = when (_userInfoState.value) {
                    null -> ActivationState.DEACTIVATED
                    else -> {
                            ActivationState.ACTIVATED
                    }
                }
            }
        }
    }
    private val _privacyState: MutableStateFlow<Boolean> = MutableStateFlow(false)
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


    val vpnState = VpnClient.instance.getState()
    val userInfoState: StateFlow<UserInfo?> = _userInfoState.asStateFlow()
    val activatedState: StateFlow<ActivationState> = _activatedState.asStateFlow()
    val lastErrorState = VpnClient.instance.getLastError()
    val privacySate: StateFlow<Boolean> = _privacyState.asStateFlow()
    fun clearLastError() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.clearLastError()
            }
        }
    }




    private val _locationsState: MutableStateFlow<List<Location>> = MutableStateFlow(emptyList())
    fun updateLocationsState(api: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _locationsState.value = VpnClient.instance.getLocations(api)
            }

        }
    }

    val locationsState: StateFlow<List<Location>> = _locationsState.asStateFlow()
    fun onLocationSelected(locationId: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.updateLocation(locationId)
            }
            updateUserInfoState()
        }
    }

    suspend fun getTraffic(): Traffic {
          return   VpnClient.instance.getTraffic()
    }

    private val _logs = mutableStateListOf<LogLine>()
    val logs: List<LogLine> = _logs

    fun updateProtocolType(protocolType: ProtocolType) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.updateProtocolType(protocolType)
                updateUserInfoState()
            }
        }
    }

    fun activate(key: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _activatedState.value = ActivationState.ACTIVATING
                VpnClient.instance.activate(key)
                updateUserInfoState()
            }
        }
    }

    fun deactivate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                VpnClient.instance.deactivate()
                updateUserInfoState()
            }
        }
    }

    fun onVpnSwitch(on: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (on) {
                    if (vpnState.value == VpnState.Down) {
                        VpnClient.instance.start()
                    }
                } else {
                    VpnClient.instance.stop()
                }
            }
        }
    }

    fun bindActivity(activity: ComponentActivity){
        VpnClient.instance.bindActivity(activity)
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateLocationsState()
                updateUserInfoState()
                updatePrivacyState()
            }

            VpnClient.instance.getLogEntries()
                .flowOn(Dispatchers.IO)
                .collect { entries ->
                    _logs.addAll(entries)
                    if (_logs.size > 1000) {
                        _logs.removeRange(0, _logs.size - 1000)
                    }
                }
        }
    }
}