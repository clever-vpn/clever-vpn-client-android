package com.clevervpn.app.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.clevervpn.app.ui.common.ActivationState
import com.clevervpn.app.ui.screen.ActivationScreen
import com.clevervpn.app.ui.screen.HomeScreen
import com.clevervpn.app.ui.screen.LogScreen
import com.clevervpn.app.ui.screen.SettingsScreen
import com.clevervpn.app.ui.viewmodels.VpnViewModel

enum class VpnScreen() {
    Activation,
    Home,
    Settings,
    Logs,
}

@Composable
fun VpnApp(
    vm: VpnViewModel,
//    windowSize: WindowSizeClass,
//    displayFeatures: List<DisplayFeature>,
    showSnackBar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val activatedState by vm.activatedState.collectAsStateWithLifecycle()
    val privacyState by vm.privacySate.collectAsStateWithLifecycle()
    val lastErrorState by vm.lastErrorState.collectAsStateWithLifecycle()
    val userInfo by vm.userInfoState.collectAsStateWithLifecycle()
    val vpnState by vm.vpnState.collectAsStateWithLifecycle()
    val locations by vm.locationsState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()

    // error dialogs
    lastErrorState?.let { error ->
        AlertDialog(
            onDismissRequest = { vm.clearLastError() },
            title = { Text("Vpn Error") },
            text = { Text(error.message) },
            confirmButton = {
                TextButton(onClick = { vm.clearLastError() }) {
                    Text("OK")
                }
            }
        )
    }

    val startDestination: String = when(activatedState) {
        ActivationState.ACTIVATED -> VpnScreen.Home.name
        else -> VpnScreen.Activation.name
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = VpnScreen.Activation.name) {
            ActivationScreen(activatedState,
                privacyState,
                onPrivacyChecked = { state ->
                    vm.updatePrivacyState(state)
                }
                ) { key ->
                vm.activate(key)
            }
        }
        composable(route = VpnScreen.Home.name) {
            HomeScreen(
                vpnState = vpnState,
                onVpnSwitch = vm::onVpnSwitch,
                locationId = userInfo?.locationId,
                locations = locations,
                getTraffic = vm::getTraffic,
                onLocationSelected = vm::onLocationSelected,
                onUpdateLocations = { vm.updateLocationsState(true) },
                onSettings = {
                    navController.navigate(VpnScreen.Settings.name)
                },
            )
        }
        composable(route = VpnScreen.Settings.name) {
            SettingsScreen(
                userInfo = userInfo,
                onDeactivate = {
                    vm.deactivate()
                },
                onUpdateProtocolType = {
                    vm.updateProtocolType(it)
                },
                onBack = {
                    navController.navigateUp()
                },
                onLogs = {
                    navController.navigate(VpnScreen.Logs.name)
                }
            )
        }
        composable(route = VpnScreen.Logs.name) {
            LogScreen(
                logList = vm.logs,
                onBack = {
                    navController.navigateUp()
                },
                showSnackBar = showSnackBar,
            )
        }

    }
}