package com.clevervpn.app.ui.screen

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.RemoveModerator
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.clevervpn.app.R
import com.clevervpn.app.ui.components.HomeCardDivider
import com.clevervpn.app.ui.components.LineSelector
import com.clevervpn.app.ui.common.isStarted
import com.clevervpn.app.ui.common.switchChecked
import com.clevervpn.app.ui.common.switchEnabled
import com.clevervpn.app.ui.components.Logo
import com.clevervpn.app.ui.components.StatsCard
import com.clevervpn.kit.common.Line
import com.clevervpn.kit.common.Status
import com.clevervpn.kit.common.Traffic
import kotlinx.coroutines.delay

@Preview
@Composable
fun HomeScreenDownPreview() {
    HomeScreen(
        vpnState = Status.Stopped,
        onVpnSwitch = {},
        lineId = null,
        lines = emptyList(),
        startedAt = null,
        traffic = Traffic(100, 100, 1000, 1000, true),
        onSubscribeTraffic = {},
        onUnsubscribeTraffic = {},
        onUpdateLines = {},
        onLineSelected = {},
        notificationPermissionPrompted = false,
        onMarkNotificationPermissionPrompted = {},
    ) {}
}


@Preview
@Composable
fun HomeScreenUpPreview() {
    HomeScreen(
        vpnState = Status.Started,
        onVpnSwitch = {},
        lineId = null,
        lines = emptyList(),
        startedAt = null,
        traffic = Traffic(100, 100, 1000, 1000, true),
        onSubscribeTraffic = {},
        onUnsubscribeTraffic = {},
        onUpdateLines = {},
        onLineSelected = {},
        notificationPermissionPrompted = false,
        onMarkNotificationPermissionPrompted = {},

    ) {}
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vpnState: Status,
    onVpnSwitch: (Boolean) -> Unit,
    lineId: Int?,
    lines: List<Line>,
    startedAt: Long?,
    traffic: Traffic?,
    onSubscribeTraffic: () -> Unit,
    onUnsubscribeTraffic: () -> Unit,
    onUpdateLines: () -> Unit,
    onLineSelected: (Int?) -> Unit,
    notificationPermissionPrompted: Boolean,
    onMarkNotificationPermissionPrompted: () -> Unit,
    onSettings: () -> Unit,
) {
    val context = LocalContext.current
    var showVpnPermissionDialog by remember { mutableStateOf(false) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onVpnSwitch(true)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    val notificationPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(notificationPermissionGranted, notificationPermissionPrompted) {
        if (notificationPermissionGranted) {
            showNotificationPermissionDialog = false
        } else if (!notificationPermissionPrompted) {
            showNotificationPermissionDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        },

    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            HomeCard(
                modifier = Modifier.weight(0.65f),
                vpnState= vpnState,
                onVpnSwitchRequest = { checked ->
                    if (checked) {
                        val permissionIntent = VpnService.prepare(context)
                        if (permissionIntent == null) {
                            onVpnSwitch(true)
                        } else {
                            showVpnPermissionDialog = true
                        }
                    } else {
                        onVpnSwitch(false)
                    }
                },
                lineId = lineId,
                lines = lines,
                startedAt = startedAt,
                onUpdateLines = onUpdateLines,
                onLineSelected = onLineSelected
            )

            TrafficCard(
                modifier = Modifier.weight(0.35f),
                vpnState = vpnState,
                traffic = traffic,
                onSubscribeTraffic = onSubscribeTraffic,
                onUnsubscribeTraffic = onUnsubscribeTraffic,
            )

            // Add more UI components here as needed
        }

        PermissionDialogs(
            showVpnPermissionDialog = showVpnPermissionDialog,
            onDismissVpnPermissionDialog = {
                showVpnPermissionDialog = false
            },
            onConfirmVpnPermissionDialog = {
                showVpnPermissionDialog = false
                val intent = VpnService.prepare(context)
                if (intent == null) {
                    onVpnSwitch(true)
                } else {
                    vpnPermissionLauncher.launch(intent)
                }
            },
            showNotificationPermissionDialog = showNotificationPermissionDialog,
            onDismissNotificationPermissionDialog = {
                showNotificationPermissionDialog = false
                onMarkNotificationPermissionPrompted()
            },
            onConfirmNotificationPermissionDialog = {
                showNotificationPermissionDialog = false
                onMarkNotificationPermissionPrompted()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        )

    }

}

@Composable
fun HomeCard(
    modifier: Modifier,
    vpnState: Status,
    onVpnSwitchRequest: (Boolean) -> Unit,
    lineId: Int?,
    lines: List<Line>,
    startedAt: Long?,
    onUpdateLines: () -> Unit,
    onLineSelected: (lineId: Int?) -> Unit,
) {
    val connectedSeconds by produceState(initialValue = 0L, vpnState, startedAt) {
        while (true) {
            value = if (vpnState.isStarted() && startedAt != null) {
                ((System.currentTimeMillis() - startedAt) / 1000).coerceAtLeast(0)
            } else {
                0L
            }
            delay(1000)
        }
    }

    Card(
        modifier = modifier
//            .heightIn(400.dp, 600.dp)
//            .widthIn(400.dp, 600.dp)
//            .fillMaxSize()
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val modifier1 = Modifier
                    .fillMaxHeight(0.25f)
                    .aspectRatio(1f)
                if (vpnState.isStarted()) {
                    Icon(
                        Icons.Outlined.VerifiedUser,
                        contentDescription = stringResource(R.string.vpn_connected_desc),
                        tint = Color(0xFF4CAF50),
                        modifier = modifier1)
                } else {
                    Icon(
                        Icons.Outlined.RemoveModerator,
                        contentDescription = stringResource(R.string.vpn_disconnected_desc),
                        tint = Color(0xFF9E9E9E),
                        modifier = modifier1)
                }


                HomeCardDivider(
                    vpnState = vpnState,
                    connectedSeconds = connectedSeconds,
                )
                LineSelector(
                    vpnState = vpnState,
                    lineId = lineId,
                    lines = lines,
                    onUpdateLines = onUpdateLines,
                    onLineSelected = onLineSelected,
                )
                Switch(
                    enabled = vpnState.switchEnabled(),
                    checked = vpnState.switchChecked(),
                    onCheckedChange = onVpnSwitchRequest
                )
//                Text("state:${vpnState}")
            }
        }
    }
    // Implement the HomeCard UI here
    // This could be a card with some information or actions related to the VPN
}

@Composable
private fun PermissionDialogs(
    showVpnPermissionDialog: Boolean,
    onDismissVpnPermissionDialog: () -> Unit,
    onConfirmVpnPermissionDialog: () -> Unit,
    showNotificationPermissionDialog: Boolean,
    onDismissNotificationPermissionDialog: () -> Unit,
    onConfirmNotificationPermissionDialog: () -> Unit,
) {
    if (showVpnPermissionDialog) {
        AlertDialog(
            onDismissRequest = onDismissVpnPermissionDialog,
            title = { Text(stringResource(R.string.vpn_permission_title)) },
            text = { Text(stringResource(R.string.vpn_permission_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmVpnPermissionDialog) {
                    Text(stringResource(R.string.continue_label))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissVpnPermissionDialog) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showNotificationPermissionDialog) {
        AlertDialog(
            onDismissRequest = onDismissNotificationPermissionDialog,
            title = { Text(stringResource(R.string.notification_permission_title)) },
            text = { Text(stringResource(R.string.notification_permission_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmNotificationPermissionDialog) {
                    Text(stringResource(R.string.go_authorize))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissNotificationPermissionDialog) {
                    Text(stringResource(R.string.later))
                }
            }
        )
    }
}

@Composable
fun TrafficCard(
    modifier: Modifier,
    vpnState: Status,
    traffic: Traffic?,
    onSubscribeTraffic: () -> Unit,
    onUnsubscribeTraffic: () -> Unit,
){
    Card(
        modifier = modifier
//            .heightIn(400.dp, 600.dp)
//            .widthIn(400.dp, 600.dp)
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (vpnState.isStarted()) {
                DisposableEffect(Unit) {
                    onSubscribeTraffic()
                    onDispose {
                        onUnsubscribeTraffic()
                    }
                }

                StatsCard(
                    traffic = traffic
                )
            } else {
                Logo(sizeRate = 0.4f)
            }

        }
    }


}
