package com.clevervpn.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.clevervpn.kit.common.Location
import com.clevervpn.kit.common.VpnState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(
    vpnState: VpnState,
    locationId: Int?,
    locations: List<Location>,
    onUpdateLocations: () -> Unit,
    onLocationSelected: (Int?) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val locationsEx = listOf(Location(id = -1, code = "", label="auto location" )) + locations
    val locationIdEx = locationId ?: -1
    val selectedLocation = locationsEx.find { it.id == locationIdEx }

    Button(
        onClick = { showBottomSheet = true },
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FlagByCountryCode(selectedLocation?.code)
            Text(
                text = selectedLocation?.label ?: "",
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Visible
            )
            if (vpnState == VpnState.Connecting) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(12.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "",
                    modifier = Modifier.size(15.dp).clickable {
                        showBottomSheet = true
                    },
                    tint = Color.Blue
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            scrimColor = Color.Transparent,

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 16.dp)

            ) {
                IconButton(
                    onClick = {
                        onUpdateLocations()
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp).align(Alignment.End),
                ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(locationsEx.size) { index ->
                        val location = locationsEx[index]
                        Row(
                            modifier = Modifier.fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                onLocationSelected(if (location.id == -1) null else location.id)
                                showBottomSheet = false
                            },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // 第一、二元素（固定间隔左对齐）
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(16.dp) // 固定间距
                            ) {
                                FlagByCountryCode(location.code)
                                Text(
                                    text = location.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    overflow = TextOverflow.Visible
                                )

                            }

                            if (locationIdEx == location.id) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "selected",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Green
                                )
                            }
                        }

                    }
                }


            }



        }
    }
}