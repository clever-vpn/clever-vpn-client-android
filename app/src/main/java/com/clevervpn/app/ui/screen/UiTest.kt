package com.clevervpn.app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.clevervpn.app.ui.viewmodels.VpnViewModel
import com.clevervpn.kit.VpnClient
import com.clevervpn.kit.common.LogLine
import com.clevervpn.kit.common.ProtocolType
import java.util.Date
import android.util.Log
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext


@Composable
fun UiTest(vm: VpnViewModel) {
    val userInfo = vm.userInfoState.collectAsState()
//    val isActivated by  remember { derivedStateOf { userInfo.value != null } }
    val isActivated by vm.activatedState.collectAsState()
    val context = LocalContext.current
    var keyInput by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
//    val logs = remember {
//            mutableStateListOf<LogLine>(LogLine(1,1, Date(), "1234567890", "xxxx", "dddd"))
//    }
//    LaunchedEffect(Unit) {
//            VpnClient.instance.getLogEntries().flowOn(Dispatchers.IO).collect {
//                withContext(Dispatchers.Main) {
//                    it.forEach { x ->
//
//                        logs.add(x)
//
//                    }
//                }
//            }
//    }

    Column {
        ProtocolType.entries.forEach { protocol ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable {
                    vm.updateProtocolType(protocol)
                }
            ) {
                RadioButton(
                    selected = (protocol == userInfo.value?.protocolType),
                    onClick = { vm.updateProtocolType(protocol) }
                )
                Text(
                    text = protocol.name,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

//        if (isActivated) {
//            Button(onClick = {
//                    vm.deactivate()
//                }) {
//                Text(text = "Deactivate")
//            }
//        }else
//        {
//
//            OutlinedTextField(
//                value = keyInput,
//                onValueChange = { keyInput = it },
//                label = { Text("请输入 Key") },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 提交按钮
//            Button(
//                onClick = {
//                    vm.activate(keyInput)
//                },
//                enabled = keyInput.isNotBlank() // 输入非空时启用按钮
//            ) {
//                Text("提交")
//            }
//
//        }


        Button (onClick = {
            Log.i("TEST", "Hello")
        }) {
            Text(text = "Log test")
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top
        ) {

            items(vm.logs) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                    text = it.time.toString(),
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
                            withStyle(style= SpanStyle(color = when(it.level) {
                                "V", "D" -> Color.Gray
                                "E" -> Color.Red
                                "I" -> Color.Green
                                "W" -> Color.Yellow
                                else -> Color.Black
                            }, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)) {
                                append(it.tag)
                            }
                            append(" [${it.msg}]")
                        }
                    )
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
            }

        }
        
    }
}


@Composable
fun Test1() {
    var count by remember { mutableStateOf(0) } // 状态
    val doubledCount = count * 2 // 计算变量（依赖状态）
    Column {
        Text(text = "Count: $doubledCount") // 显示计算值
        Button(onClick = {
            count += 1
        }) {
            Text("Add")
        }
    }
}


@Preview
@Composable
fun MyTest(){
    Test1()
//    Text("hi")
}



