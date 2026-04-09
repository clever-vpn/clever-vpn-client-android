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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clevervpn.app.ui.viewmodels.VpnViewModel
import com.clevervpn.kit.common.Protocol
import android.util.Log
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun UiTest(vm: VpnViewModel) {
    val userInfo = vm.userInfoState.collectAsState()
    val logs by vm.logState.collectAsState()
    var keyInput by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    DisposableEffect(Unit) {
        vm.subscribeLogs()
        onDispose {
            vm.unsubscribeLogs()
        }
    }

    Column {
        Protocol.entries.forEach { protocol ->
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

            items(logs) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                    text = "L${it.level}",
                    modifier = Modifier.weight(1f),
                    color = Color.Gray
                        )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style= SpanStyle(color = when(it.level) {
                                0, 1 -> Color.Gray
                                2 -> Color.Green
                                3 -> Color.Yellow
                                4, 5 -> Color.Red
                                else -> Color.Black
                            }, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)) {
                                append("Level ${it.level}")
                            }
                            append(" [${it.message}]")
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



