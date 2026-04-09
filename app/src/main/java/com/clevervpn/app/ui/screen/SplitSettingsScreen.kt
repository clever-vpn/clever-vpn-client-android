package com.clevervpn.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clevervpn.app.R
import com.clevervpn.kit.common.DomainRule
import com.clevervpn.kit.common.IpRule
import com.clevervpn.kit.common.Region
import com.clevervpn.kit.common.Split

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitSettingsScreen(
    splitInfo: Split,
    onSave: (Split) -> Unit,
    onRefreshRegionCode: () -> Unit,
    onBack: () -> Unit,
) {
    var regionEnabled by remember(splitInfo.region.enable) { mutableStateOf(splitInfo.region.enable) }
    var ipEnabled by remember(splitInfo.ip.enable) { mutableStateOf(splitInfo.ip.enable) }
    var domainEnabled by remember(splitInfo.domain.enable) { mutableStateOf(splitInfo.domain.enable) }

    val defaultDomainLines = remember(splitInfo.domain.domain, splitInfo.domain.domainSuffix) {
        buildList {
            addAll(splitInfo.domain.domain)
            addAll(splitInfo.domain.domainSuffix.map { suffix ->
                if (suffix.startsWith(".")) suffix else ".${suffix}"
            })
        }.joinToString("\n")
    }

    var ipInput by remember(splitInfo.ip.ipCidr) {
        mutableStateOf(splitInfo.ip.ipCidr.joinToString("\n"))
    }
    var domainInput by remember(defaultDomainLines) {
        mutableStateOf(defaultDomainLines)
    }
    var regexInput by remember(splitInfo.domain.domainRegex) {
        mutableStateOf(splitInfo.domain.domainRegex.joinToString("\n"))
    }
    var advancedExpanded by remember { mutableStateOf(false) }

    val latestSplit = rememberUpdatedState(
        newValue = Split(
            region = Region(
                enable = regionEnabled,
                code = splitInfo.region.code,
            ),
            ip = IpRule(
                enable = ipEnabled,
                ipCidr = splitLines(ipInput),
            ),
            domain = DomainRule(
                enable = domainEnabled,
                domain = splitLines(domainInput).filter { !it.startsWith(".") },
                domainSuffix = splitLines(domainInput)
                    .filter { it.startsWith(".") }
                    .map { it.removePrefix(".") }
                    .filter { it.isNotBlank() },
                domainRegex = splitLines(regexInput),
            ),
        )
    )

    DisposableEffect(Unit) {
        onDispose {
            onSave(latestSplit.value)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.split_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SplitSectionCard(
                title = stringResource(R.string.split_region),
                enabled = regionEnabled,
                onEnabledChange = { regionEnabled = it },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            R.string.split_current_code,
                            displayValue(splitInfo.region.code, stringResource(R.string.none_text))
                        )
                    )
                    IconButton(onClick = onRefreshRegionCode) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh_region_code),
                        )
                    }
                }
            }

            SplitSectionCard(
                title = stringResource(R.string.split_ip),
                enabled = ipEnabled,
                onEnabledChange = { ipEnabled = it },
            ) {
                OutlinedTextField(
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text(stringResource(R.string.split_ip_label)) },
                    placeholder = { Text(stringResource(R.string.split_ip_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            }

            SplitSectionCard(
                title = stringResource(R.string.split_domain),
                enabled = domainEnabled,
                onEnabledChange = { domainEnabled = it },
            ) {
                OutlinedTextField(
                    value = domainInput,
                    onValueChange = { domainInput = it },
                    label = { Text(stringResource(R.string.split_domain_label)) },
                    placeholder = { Text(stringResource(R.string.split_domain_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(R.string.split_advanced_regex))
                    IconButton(onClick = { advancedExpanded = !advancedExpanded }) {
                        Icon(
                            imageVector = if (advancedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (advancedExpanded) {
                                stringResource(R.string.collapse)
                            } else {
                                stringResource(R.string.expand)
                            },
                        )
                    }
                }

                if (advancedExpanded) {
                    OutlinedTextField(
                        value = regexInput,
                        onValueChange = { regexInput = it },
                        label = { Text(stringResource(R.string.split_regex_label)) },
                        placeholder = { Text(stringResource(R.string.split_regex_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                    )
                }
            }
        }
    }
}

@Composable
private fun SplitSectionCard(
    title: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(text = title)
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }
            content()
        }
    }
}

private fun splitLines(input: String): List<String> {
    return input
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .toList()
}

private fun displayValue(value: String, noneText: String): String {
    return if (value.isBlank()) noneText else value
}
