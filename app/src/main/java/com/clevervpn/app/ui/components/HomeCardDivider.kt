package com.clevervpn.app.ui.components

import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clevervpn.app.utils.msTimerString
import com.clevervpn.kit.common.VpnState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCardDivider(vpnState: VpnState) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        when (vpnState) {
            VpnState.Down -> {
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
                                text = "Vpn is off",
                                fontSize = 18.sp
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.weight(1f))
                }

//                HorizontalDivider(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 35.dp),
//                    thickness = 2.dp,
//                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f)
//                )
            }

            VpnState.Connecting, VpnState.Reconnecting -> LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp),
            )

            is VpnState.Up -> {
                var elapsedTimeMs by remember {
                    mutableLongStateOf(SystemClock.elapsedRealtime() - vpnState.startTime)
                }
                LaunchedEffect(key1 = vpnState.startTime) {
                    while (true) {
                        delay(1000)
                        elapsedTimeMs = SystemClock.elapsedRealtime() - vpnState.startTime
                    }
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
                                text = elapsedTimeMs.msTimerString(),
                                fontSize = 18.sp
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
