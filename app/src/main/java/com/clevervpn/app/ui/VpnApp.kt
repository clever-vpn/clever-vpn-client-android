package com.clevervpn.app.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.clevervpn.app.R
import com.clevervpn.app.ui.common.ActivationState
import com.clevervpn.app.ui.screen.ActivationScreen
import com.clevervpn.app.ui.screen.HomeScreen
import com.clevervpn.app.ui.screen.LogScreen
import com.clevervpn.app.ui.screen.SettingsScreen
import com.clevervpn.app.ui.screen.SplitSettingsScreen
import com.clevervpn.app.ui.viewmodels.VpnViewModel
import com.clevervpn.kit.common.Split

enum class VpnScreen() {
    Activation,
    Home,
    Settings,
    SplitSettings,
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
    val traffic by vm.trafficState.collectAsStateWithLifecycle()
    val logs by vm.logState.collectAsStateWithLifecycle()
    val connInfo by vm.connInfoState.collectAsStateWithLifecycle()
    val lines by vm.linesState.collectAsStateWithLifecycle()
    val startedAt by vm.startedAtState.collectAsStateWithLifecycle()
    val splitInfo = userInfo?.splitInfo ?: Split()
    val notificationPermissionPrompted by vm.notificationPermissionPrompted.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()

    // error dialogs
    lastErrorState?.let { error ->
        AlertDialog(
            onDismissRequest = { vm.clearLastError() },
            title = { Text(stringResource(R.string.vpn_error_title)) },
            text = { Text(error.message) },
            confirmButton = {
                TextButton(onClick = { vm.clearLastError() }) {
                    Text(stringResource(R.string.ok))
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
                lineId = if (userInfo?.line == 0) null else userInfo?.line,
                lines = lines,
                startedAt = startedAt,
                traffic = traffic,
                onSubscribeTraffic = vm::subscribeTraffic,
                onUnsubscribeTraffic = vm::unsubscribeTraffic,
                onLineSelected = vm::onLineSelected,
                onUpdateLines = vm::refreshLines,
                notificationPermissionPrompted = notificationPermissionPrompted,
                onMarkNotificationPermissionPrompted = vm::markNotificationPermissionPrompted,
                onSettings = {
                    navController.navigate(VpnScreen.Settings.name)
                },
            )
        }
        composable(route = VpnScreen.Settings.name) {
            SettingsScreen(
                userInfo = userInfo,
                connInfo = connInfo,
                onDeactivate = {
                    vm.deactivate()
                },
                onUpdateProtocolType = {
                    vm.updateProtocolType(it)
                },
                onSplitSettings = {
                    navController.navigate(VpnScreen.SplitSettings.name)
                },
                onBack = {
                    navController.navigateUp()
                },
                onLogs = {
                    navController.navigate(VpnScreen.Logs.name)
                }
            )
        }
        composable(route = VpnScreen.SplitSettings.name) {
            SplitSettingsScreen(
                splitInfo = splitInfo,
                onSave = { vm.updateSplitInfo(it) },
                onRefreshRegionCode = vm::updateRegionCode,
                onBack = { navController.navigateUp() },
            )
        }
        composable(route = VpnScreen.Logs.name) {
            LogScreen(
                logList = logs,
                onSubscribeLogs = vm::subscribeLogs,
                onUnsubscribeLogs = vm::unsubscribeLogs,
                onBack = {
                    navController.navigateUp()
                },
                showSnackBar = showSnackBar,
            )
        }

    }
}