package com.clevervpn.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clevervpn.app.utils.prettyBytes
import com.clevervpn.kit.common.Traffic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun Stats(value: String, title: String, isDownload: Boolean = false) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isDownload) Icons.Default.ArrowCircleDown else Icons.Default.ArrowCircleUp,
            "Downloaded", modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.size(2.dp))
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Normal)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsCard(getTraffic: suspend () -> Traffic?) {
    var traffic by remember { mutableStateOf<Traffic?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while(true) {
                traffic = getTraffic()
                delay(1000)
            }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Stats(prettyBytes((traffic?.rx ?: 0L).toULong()), "DOWNLOADED", isDownload = true)
        VerticalDivider(modifier = Modifier.padding(vertical = 15.dp))
        Stats(prettyBytes((traffic?.tx ?: 0L).toULong()), "UPLOADED")
    }


}


@Preview
@Composable
fun PreviewStats() {
    Stats("12.23KiB", "DOWNLOADED")
}

@Preview(showSystemUi = true)
@Composable
fun PreviewStatsCard() {

    StatsCard { Traffic(1223, 1223)}
}

//@Preview
//@Composable
//fun SplitScreenLayout() {
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // 上部区域 - 占65%
//        Card(
//            modifier = Modifier
//                .weight(0.65f)
//                .fillMaxWidth()
//                .background(Color.LightGray)
//        ) {
//            // 这里放置上部内容
//            Text(
//                text = "上部区域 (65%)",
//                modifier = Modifier.align(Alignment.CenterHorizontally),
//                fontSize = 24.sp
//            )
//        }
//
//        // 下部区域 - 占35%
//        Box(
//            modifier = Modifier
//                .weight(0.35f)
//                .fillMaxWidth()
//                .background(Color.Gray)
//        ) {
//            // 这里放置下部内容
//            Text(
//                text = "下部区域 (35%)",
//                modifier = Modifier.align(Alignment.Center),
//                fontSize = 24.sp
//            )
//        }
//    }
//}
