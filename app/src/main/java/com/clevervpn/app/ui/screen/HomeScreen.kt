package com.clevervpn.app.ui.screen

import android.annotation.SuppressLint
import android.os.SystemClock
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clevervpn.app.ui.components.HomeCardDivider
import com.clevervpn.app.ui.components.LocationSelector
import com.clevervpn.app.ui.common.switchChecked
import com.clevervpn.app.ui.common.switchEnabled
import com.clevervpn.app.ui.components.Logo
import com.clevervpn.app.ui.components.StatsCard
import com.clevervpn.kit.common.Location
import com.clevervpn.kit.common.Traffic
import com.clevervpn.kit.common.VpnState

@Preview
@Composable
fun HomeScreenDownPreview() {
    HomeScreen(
        vpnState = VpnState.Down,
        onVpnSwitch = {},
        locationId = null,
        locations = emptyList(),
        getTraffic = { Traffic(100, 100) },
        onUpdateLocations = {},
        onLocationSelected = {},
    ) {}
}


@Preview
@Composable
fun HomeScreenUpPreview() {
    HomeScreen(
        vpnState = VpnState.Up(SystemClock.elapsedRealtime()),
        onVpnSwitch = {},
        locationId = null,
        locations = emptyList(),
        getTraffic = { Traffic(100, 100) },
        onUpdateLocations = {},
        onLocationSelected = {},

    ) {}
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vpnState: VpnState,
    onVpnSwitch: (Boolean) -> Unit,
    locationId: Int?,
    locations: List<Location>,
    getTraffic: suspend () -> Traffic,
    onUpdateLocations: () -> Unit,
    onLocationSelected: (Int?) -> Unit,
    onSettings: () -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Clever VPN") },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                onVpnSwitch = onVpnSwitch,
                locationId = locationId,
                locations = locations,
                onUpdateLocations = onUpdateLocations,
                onLocationSelected = onLocationSelected
            )

            TrafficCard(
                modifier = Modifier.weight(0.35f),
                vpnState = vpnState,
                getTraffic = getTraffic
            )

            // Add more UI components here as needed
        }

    }

}

@Composable
fun HomeCard(
    modifier: Modifier,
    vpnState: VpnState,
    onVpnSwitch: (Boolean) -> Unit,
    locationId: Int?,
    locations: List<Location>,
    onUpdateLocations: () -> Unit,
    onLocationSelected: (locationId: Int?) -> Unit,
) {
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
                if (vpnState is VpnState.Up) {
                    Icon(
                        Icons.Outlined.VerifiedUser,
                        contentDescription = "vpn connected",
                        tint = Color(0xFF4CAF50),
                        modifier = modifier1)
                } else {
                    Icon(
                        Icons.Outlined.RemoveModerator,
                        contentDescription = "vpn disconnected",
                        tint = Color(0xFF9E9E9E),
                        modifier = modifier1)
                }


                HomeCardDivider(vpnState)
                LocationSelector(
                    vpnState = vpnState,
                    locationId = locationId,
                    locations = locations,
                    onUpdateLocations = onUpdateLocations,
                    onLocationSelected = onLocationSelected,
                )
                Switch(
                    enabled = vpnState.switchEnabled(),
                    checked = vpnState.switchChecked(),
                    onCheckedChange = onVpnSwitch
                )
//                Text("state:${vpnState}")
            }
        }
    }
    // Implement the HomeCard UI here
    // This could be a card with some information or actions related to the VPN
}

@Composable
fun TrafficCard(
    modifier: Modifier,
    vpnState: VpnState,
    getTraffic: suspend () -> Traffic,
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
            if (vpnState is VpnState.Up) {
                StatsCard(
                    getTraffic = getTraffic
                )
            } else {
                Logo(sizeRate = 0.4f)
            }

        }
    }


}
