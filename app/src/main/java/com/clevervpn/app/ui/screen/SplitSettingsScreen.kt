package com.clevervpn.app.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.clevervpn.kit.common.SplitRegionOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitSettingsScreen(
    splitInfo: Split,
    onSave: (Split) -> Unit,
    onBack: () -> Unit,
) {
    var regionEnabled by remember(splitInfo.region.enable) { mutableStateOf(splitInfo.region.enable) }
    // region 代码**永远不为空**：落盘值空/非法 ⇒ 用缺省（cn）。
    var regionCode by remember(splitInfo.region.code) {
        mutableStateOf(SplitRegionOptions.normalize(splitInfo.region.code) ?: SplitRegionOptions.DEFAULT_CODE)
    }
    var showRegionList by remember { mutableStateOf(false) }
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
                code = regionCode,
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

    if (showRegionList) {
        // 二级列表（仿系统「设置 → 语言」）：可滚动、按大洲分组、点选即生效并返回。
        RegionSelectionScreen(
            selectedCode = regionCode,
            onSelect = { code ->
                regionCode = code
                showRegionList = false
            },
            onBack = { showRegionList = false },
        )
        return
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
                onEnabledChange = { enabled ->
                    regionEnabled = enabled
                    // 开启时地区必须有一个值：没有就落缺省（cn）；已有值保留用户的。
                    if (enabled) {
                        regionCode = SplitRegionOptions.normalize(regionCode)
                            ?: SplitRegionOptions.DEFAULT_CODE
                    }
                },
            ) {
                // 选择控件只在开启时才显示（关闭时"选地区"没有意义；值仍保留在 userInfo 里）。
                if (regionEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRegionList = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(R.string.split_region_current))
                        Text(
                            text = SplitRegionOptions.label(regionCode),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
