package com.clevervpn.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clevervpn.app.ui.common.isStarted
import com.clevervpn.app.ui.common.isTransitioning
import com.clevervpn.kit.common.Status

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCardDivider(
    vpnState: Status,
    connectedSeconds: Long,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        if (vpnState.isTransitioning()) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp),
            )
        } else {
            val (label, chipColor) = if (vpnState.isStarted()) {
                formatConnectedDuration(connectedSeconds) to MaterialTheme.colorScheme.onPrimary
            } else {
                "Vpn is off" to MaterialTheme.colorScheme.surfaceContainer
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp)
            ) {
                HorizontalDivider(thickness = 2.dp, modifier = Modifier.weight(1f))
                SuggestionChip(
                    onClick = { },
                    label = {
                        Text(
                            text = label,
                            fontSize = 18.sp
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = chipColor
                    )
                )
                HorizontalDivider(thickness = 2.dp, modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun formatConnectedDuration(totalSeconds: Long): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    val hours = safeSeconds / 3600
    val minutes = (safeSeconds % 3600) / 60
    val seconds = safeSeconds % 60

    return when {
        hours > 0 -> String.format("%dh %02dm %02ds", hours, minutes, seconds)
        minutes > 0 -> String.format("%dm %02ds", minutes, seconds)
        else -> String.format("%ds", seconds)
    }
}
