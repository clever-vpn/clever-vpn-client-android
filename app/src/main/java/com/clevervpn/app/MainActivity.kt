package com.clevervpn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clevervpn.app.ui.VpnApp
import com.clevervpn.app.ui.theme.CleverVPNTheme
import com.clevervpn.app.ui.viewmodels.VpnViewModel
import com.google.accompanist.adaptive.calculateDisplayFeatures
import kotlinx.coroutines.launch
import com.clevervpn.app.ui.screen.UiTest

class MainActivity : ComponentActivity() {
    private val vm: VpnViewModel by viewModels()
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.init(this)
        vm.bindActivity(this)
        enableEdgeToEdge()
        setContent {
            CleverVPNTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    // A surface container using the 'background' color from the theme
                    val snackbarHostState = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()
                    val displayFeatures = calculateDisplayFeatures(activity = this)

                    val showSnackBar: (msg: String) -> Unit = { msg ->
                        scope.launch {
                            snackbarHostState.showSnackbar(msg)
                        }
                    }

//                    val windowSize = calculateWindowSizeClass(this)

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 80.dp)
                            )
                        }) {
                        VpnApp(
                            vm = vm,
//                            windowSize = windowSize,
//                            displayFeatures = displayFeatures,
                            showSnackBar = showSnackBar,
                            modifier = Modifier.padding(it)
                        )
//                        println(it)
//                        UiTest(vm)
                    }
                }

//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                    Greeting(
//                        name = Utils.greet("wubolin"),
//                        modifier = Modifier.padding(innerPadding)
//                        )
//                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleverVPNTheme {
        Column {
            Greeting("Android")
        }

    }
}