package com.clevervpn.app.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.clevervpn.app.R
import com.clevervpn.kit.common.ConnInfo
import com.clevervpn.kit.common.Protocol
import com.clevervpn.kit.common.Split
import com.clevervpn.kit.common.UserInfo
import kotlinx.coroutines.delay


@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        userInfo = UserInfo(
            key = "",
            providerUrl = "https://clevervpn.example.com",
            line = 1,
            protocolType = Protocol.UDP_TUNNEL
        ),
        connInfo = null,
        onDeactivate = {},
        onUpdateProtocolType = {},
        onSplitSettings = {},
        onBack = {},
        onLogs = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userInfo: UserInfo?,
    connInfo: ConnInfo?,
    onDeactivate: () -> Unit,
    onUpdateProtocolType: (type: Protocol) -> Unit,
    onSplitSettings: () -> Unit,
    onBack: () -> Unit,
    onLogs: () -> Unit,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val providerUrl = userInfo?.providerUrl?.trim().takeUnless { it.isNullOrEmpty() } ?: "https://www.clever-vpn.net"
    var showDeActivateDlg by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2500)
            copied = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
//                actions = {
//                    IconButton(onClick = onSettings) {
//                        Icon(Icons.Default.Settings, contentDescription = "Settings")
//                    }
//                }
            )
        },

        ) { innerPadding ->

        if (userInfo == null) {
            return@Scaffold
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
//            CardSettingItem(title = "Activation Key") {
            CardSettingItem(title = stringResource(R.string.activation_key)) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = userInfo.key.chunked(4).joinToString("-"),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(userInfo.key))
                                copied = true
                                Toast.makeText(context, context.getString(R.string.activation_key_copied), Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = if (copied) {
                                    stringResource(R.string.copied)
                                } else {
                                    stringResource(R.string.copy_activation_key)
                                },
                                tint = if (copied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Button(onClick = {
                        showDeActivateDlg = true
                    }) {
                        Text(text = stringResource(R.string.deactivate))
                    }

                    if (showDeActivateDlg) {
                        ConfirmationDialog(
                            title = stringResource(R.string.warning),
                            content = stringResource(R.string.deactivate_confirm_message),
                            onConfirm = {
                                onDeactivate()
                                showDeActivateDlg = false
                            },
                            onDismiss = {
                                showDeActivateDlg = false
                            }
                        )
                    }

                }
            }
            CardSettingItem(title = stringResource(R.string.protocol_type)) {
                val protocols = arrayOf(
                    Protocol.AUTO,
                    Protocol.UDP_TUNNEL,
                    Protocol.UDP_FAST,
                    Protocol.UDP_STABLE,
                    Protocol.TCP_FAST,
                    Protocol.TCP_STABLE,
                )
                Column {
                    for (protocol in protocols) {
                        val protocolDescription = when (protocol) {
                            Protocol.AUTO -> stringResource(R.string.protocol_desc_auto)
                            Protocol.UDP_TUNNEL -> stringResource(R.string.protocol_desc_udp_tunnel)
                            Protocol.UDP_FAST -> stringResource(R.string.protocol_desc_udp_fast)
                            Protocol.UDP_STABLE -> stringResource(R.string.protocol_desc_udp_stable)
                            Protocol.TCP_FAST -> stringResource(R.string.protocol_desc_tcp_fast)
                            Protocol.TCP_STABLE -> stringResource(R.string.protocol_desc_tcp_stable)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                                .padding(8.dp).clickable {
                                    onUpdateProtocolType(protocol)
                                },
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        MaterialTheme.typography.labelLarge.toSpanStyle()
                                    ) {
                                        append(protocol.name)
                                    }
                                    withStyle(
                                        MaterialTheme.typography.bodySmall.toSpanStyle()
                                            .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    ) {
                                        append("  $protocolDescription")
                                    }

                                }
                            )
                            if (protocol == userInfo.protocolType) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.checked),
                                    modifier = Modifier.padding(4.dp),
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                    }
                }
            }
            CardSettingItem(title = stringResource(R.string.split_info)) {
                SettingsNavigationRow(
                    title = stringResource(R.string.split_rules),
                    onClick = onSplitSettings,
                )
            }
            CardSettingItem(title = stringResource(R.string.connection_info)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = stringResource(R.string.connection_protocol, displayConnInfo(connInfo?.protocol, stringResource(R.string.none_text))))
                    Text(text = stringResource(R.string.connection_relay, displayConnInfo(connInfo?.relayIP, stringResource(R.string.none_text))))
                    Text(text = stringResource(R.string.connection_upstream, displayConnInfo(connInfo?.upstream, stringResource(R.string.none_text))))
                }
            }
            CardSettingItem(title = stringResource(R.string.logs)) {
                SettingsNavigationRow(
                    title = stringResource(R.string.view_logs),
                    onClick = onLogs,
                )
            }
            CardSettingItem(title = stringResource(R.string.about_us)) {
                Text(
                    text = providerUrl,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, providerUrl.toUri())
                            context.startActivity(intent)
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Blue
                )
            }

            CardSettingItem(title = stringResource(R.string.version)) {
                    Text(text = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown")
            }
        }
    }

}

@Composable
private fun SettingsNavigationRow(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = stringResource(R.string.open),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun displayConnInfo(value: String?, noneText: String): String {
    return if (value.isNullOrBlank()) noneText else value
}

@Composable
fun CardSettingItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 标题区域
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 描述文本（可选）
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // 内容区域
            content()
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    content: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = stringResource(R.string.warning),
            )
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(content)
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}