package com.clevervpn.app.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clevervpn.app.R
import com.clevervpn.kit.common.SplitRegionGroup
import com.clevervpn.kit.common.SplitRegionOption
import com.clevervpn.kit.common.SplitRegionOptions
import com.clevervpn.kit.common.SplitRegionSection

/**
 * 地区选择：仿系统「设置 → 语言」的形态 —— **可滚动的分组列表**（按大洲分组），
 * 点选即生效并返回，当前项带勾选。所以内置列表再长（现在 31 项）都不怕。
 *
 * 语义（三端一致）：region = **用户所在的国家/地区**，只能由用户显式选择
 * （不做任何自动探测，原因见 `SplitRegionOptions` 的注释）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionSelectionScreen(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val current = remember(selectedCode) { SplitRegionOptions.option(selectedCode) }
    val sections = remember(query, current.code) { buildSections(query, current) }

    BackHandler { onBack() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.split_region)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(R.string.split_region_search)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sections.forEach { section ->
                    item(key = section.group.name) {
                        Text(
                            text = stringResource(groupTitleRes(section.group)),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 4.dp),
                        )
                    }
                    items(section.options, key = { it.code }) { option ->
                        RegionRow(
                            option = option,
                            selected = option.code == current.code,
                            onClick = { onSelect(option.code) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionRow(
    option: SplitRegionOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = option.label)
        Spacer(Modifier.weight(1f))
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** 按大洲分组（搜索时只保留命中的项）。 */
private fun buildSections(query: String, current: SplitRegionOption): List<SplitRegionSection> {
    val keyword = query.trim().lowercase()
    val result = mutableListOf<SplitRegionSection>()

    SplitRegionOptions.sections.forEach { section ->
        val options =
                if (keyword.isEmpty()) {
                    section.options
                } else {
                    section.options.filter { matches(it, keyword) }
                }
        if (options.isNotEmpty()) {
            result.add(SplitRegionSection(section.group, options))
        }
    }

    // 当前值不在内置列表里（自定义代码）时额外补一段，保证列表里总能勾出当前值。
    val listed = result.any { section -> section.options.any { it.code == current.code } }
    if (current.group == SplitRegionGroup.CUSTOM &&
                    !listed &&
                    (keyword.isEmpty() || matches(current, keyword))) {
        result.add(SplitRegionSection(SplitRegionGroup.CUSTOM, listOf(current)))
    }

    return result
}

/** 搜索按「地区代码 + 英文名」匹配（列表里只显示「🇯🇵  jp」，不塞英文名）。 */
private fun matches(option: SplitRegionOption, keyword: String): Boolean =
        option.code.contains(keyword) || option.name.lowercase().contains(keyword)

private fun groupTitleRes(group: SplitRegionGroup): Int =
        when (group) {
            SplitRegionGroup.ASIA -> R.string.split_region_group_asia
            SplitRegionGroup.AMERICAS -> R.string.split_region_group_americas
            SplitRegionGroup.EUROPE -> R.string.split_region_group_europe
            SplitRegionGroup.OCEANIA -> R.string.split_region_group_oceania
            SplitRegionGroup.CUSTOM -> R.string.split_region_group_custom
        }
