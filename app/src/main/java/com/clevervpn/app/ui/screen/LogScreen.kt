package com.clevervpn.app.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clevervpn.app.R
import com.clevervpn.app.ui.common.AnsiColorUtils
import com.clevervpn.kit.common.LogLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    logList: List<LogLine>,
    onSubscribeLogs: () -> Unit,
    onUnsubscribeLogs: () -> Unit,
    onBack: () -> Unit,
    showSnackBar: (String) -> Unit,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        onSubscribeLogs()
        onDispose {
            onUnsubscribeLogs()
        }
    }

    LaunchedEffect(logList) {
        if (logList.isNotEmpty()) {
            // 滚动到最后一个条目
            listState.animateScrollToItem(logList.size - 1)
        }
    }

    // 创建文件保存的Launcher
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 写入文件内容
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(logList.joinToString("\n") { "[L${it.level}] ${it.message}" }.toByteArray())
                    }
                    showSnackBar(context.getString(R.string.log_save_success, uri.path ?: ""))
                } catch (e: Exception) {
                    showSnackBar(context.getString(R.string.log_save_failed, e.localizedMessage ?: ""))
                }
            }
        } else {
            showSnackBar(context.getString(R.string.user_cancelled_action))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.logs)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        //save log to a file
                        val fileName = "clever_vpn_logs.txt"
                        saveFileLauncher.launch(fileName)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(R.string.save_log)
                        )
                    }
                }
            )
        },


        ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            items(logList.size) { index ->
                val logLine = logList[index]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = AnsiColorUtils.ansiToAnnotatedString(logLine.message)
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
                }
            }
        }

//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.padding(innerPadding)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(vertical = 20.dp)
//        ) {
//
//        }
    }
}