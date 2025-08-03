package com.clevervpn.app.ui.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.clevervpn.app.R
import com.clevervpn.kit.common.ProtocolType
import com.clevervpn.kit.common.UserInfo


@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        userInfo = UserInfo(
            key = "", appId = "", url = "sss",
            locationId = null,
            protocolType = ProtocolType.UDP
        ),
        onDeactivate = {},
        onUpdateProtocolType = {},
        onBack = {},
        onLogs = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userInfo: UserInfo?,
    onDeactivate: () -> Unit,
    onUpdateProtocolType: (type: ProtocolType) -> Unit,
    onBack: () -> Unit,
    onLogs: () -> Unit,
) {
    val context = LocalContext.current
    var showDeActivateDlg by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
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
                    Text(text = userInfo.key.chunked(4).joinToString("-"))
                    Button(onClick = {
                        showDeActivateDlg = true
                    }) {
                        Text(text = "DeActivate")
                    }

                    if (showDeActivateDlg) {
                        ConfirmationDialog(
                            title = "Warning",
                            content = "Are you sure to deactivate?",
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
                    ProtocolType.AUTO,
                    ProtocolType.UDP,
                    ProtocolType.KUDP,
                    ProtocolType.TCP,
                )
                val description = {
                    type: ProtocolType ->
                    when (type) {
                        ProtocolType.UDP -> "for low packet loss"
                        ProtocolType.TCP -> "for UDP not available"
                        ProtocolType.AUTO -> "auto Select Protocol (default)"
                        ProtocolType.KUDP -> "for high packet loss"
                    }
                }
                Column {
                    for (protocol in protocols) {
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
                                        append("  ${description(protocol)}")
                                    }

                                }
                            )
                            if (protocol == userInfo.protocolType) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Checked",
                                    modifier = Modifier.padding(4.dp),
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                    }
                }
            }
            CardSettingItem(title = stringResource(R.string.logs)) {
                Button(onClick = onLogs) {
                    Text(text = stringResource(R.string.view_logs))
                }
            }
            if (userInfo.url != null) {
                CardSettingItem(title = stringResource(R.string.about_us)) {
                    Text(
                        text = "${userInfo.url} ",
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, userInfo.url!!.toUri())
                                context.startActivity(intent)
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Blue
                    )
                }
            }

            CardSettingItem(title = stringResource(R.string.version)) {
                    Text(text = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown")
            }
        }
    }

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
                contentDescription = "Warning",
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
                Text("Ok")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}