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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.clevervpn.app.R
import com.clevervpn.kit.common.LogLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    logList: List<LogLine>,
    onBack: () -> Unit,
    showSnackBar: (String) -> Unit,
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

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
                        outputStream.write(logList.joinToString("\n") { "${it.time} [${it.tag}] ${it.msg}" }.toByteArray())
                    }
                    showSnackBar("保存成功！\n文件路径: ${uri.path}")
                } catch (e: Exception) {
                    showSnackBar("保存失败: ${e.localizedMessage}")
                }
            }
        } else {
            showSnackBar("用户取消操作")
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
                            contentDescription = "Go Back"
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
                            contentDescription = "save log"
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
                            text = logLine.time.toString(),
                            modifier = Modifier.weight(1f),
                            color = Color.Gray
                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        //请用AnnotatedString将两个Text合并到一个Text中
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style= SpanStyle(color = when(logLine.level) {
                                    "V", "D" -> Color.Gray
                                    "E" -> Color.Red
                                    "I" -> Color.Green
                                    "W" -> Color.Yellow
                                    else -> Color.Black
                                }, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                ) {
                                    append(logLine.tag)
                                }
                                append(" [${logLine.msg}]")
                            }
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