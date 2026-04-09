package com.clevervpn.app.ui.screen

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clevervpn.app.R
import com.clevervpn.app.ui.common.ActivationState
import com.clevervpn.app.ui.components.CameraPreview
import com.clevervpn.app.ui.components.Logo
import com.clevervpn.app.ui.components.PermissionRequestDialog
import com.clevervpn.app.ui.theme.CleverVPNTheme

@Preview(showSystemUi = true)
@Composable
fun SignInScreenNotSignedIn() {
    CleverVPNTheme {
        ActivationScreen(
            state = ActivationState.DEACTIVATED,
            privacyChecked = false,
            onPrivacyChecked = {},
            onSubmit = {}
        )
    }
}

@Composable
fun ActivationScreen(
    state: ActivationState,
    privacyChecked: Boolean,
    onPrivacyChecked: (Boolean) -> Unit,
    onSubmit: (key: String) -> Unit,
) {
    when(state) {
        ActivationState.ACTIVATED ->
            Brand()
        else -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 20.dp)
            ) {
                SignInHeader()
                SignInCard(state, privacyChecked, onPrivacyChecked, onSubmit)
            }

        }
    }
}

@Composable
fun SignInHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Logo()
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = stringResource(R.string.app_name), fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.tagline_fast_modern_vpn),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Light
        )
    }
}

@Preview
@Composable
fun Brand(
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)
    ) {
        SignInHeader()
    }
}


@Composable
fun SignInCard(
    state: ActivationState,
    privacyChecked: Boolean,
    onPrivacyChecked: (Boolean) -> Unit,
    onSubmit: (key: String) -> Unit
) {
    var key by remember { mutableStateOf("") }
    var scanCode by remember { mutableStateOf(false) }
    val scanNowText = stringResource(R.string.scan_qr_code_now)
    val noCameraPermissionText = stringResource(R.string.no_camera_permission)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            enabled = state != ActivationState.ACTIVATING,
            isError = false,
            singleLine = true,
            label = {
                Text(text = stringResource(R.string.activation_key))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            value = key,
            onValueChange = { key = it} ,
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    scanCode = true
                }) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = stringResource(R.string.scan_qr_code)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrivacyPolicySheet(
            privacyChecked = privacyChecked,
            onPrivacyChecked = onPrivacyChecked
        )

        Button(
            onClick = {onSubmit(key)},
            enabled = (
                    (state != ActivationState.ACTIVATING) &&
                            key.isNotEmpty() &&
                            privacyChecked
                    ),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (state == ActivationState.ACTIVATING) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically)
                )
            } else {
                Text(
                    text = stringResource(R.string.activate),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        var statusText by remember { mutableStateOf("") }

        PermissionRequestDialog(
            permission = Manifest.permission.CAMERA,
            onResult = { isGranted ->
                statusText = if (isGranted) {
                    scanNowText
                } else {
                    noCameraPermissionText
                }
            },
        )

        if (scanCode) {
            CameraPreview {
                key = it ?: key
                scanCode = false
            }
        }
    }
}


@Preview()
@Composable
fun PrivacyPolicySheetTest() {
    var checked by remember { mutableStateOf(false) }
    PrivacyPolicySheet(
        privacyChecked = checked,
        onPrivacyChecked = { checked = it }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicySheet(
    privacyChecked: Boolean,
    onPrivacyChecked: (Boolean) -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.privacy_sheet_agree),
            modifier = Modifier
                .padding(start = 5.dp)
                .clickable { showBottomSheet = true },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Start,
            color = Color.Blue
        )

        Switch(
            checked = privacyChecked,
            onCheckedChange = {
                onPrivacyChecked(it)
            }
        )

    }

        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                scrimColor = Color.Transparent,

            ) {
                Text(
                    text = stringResource(R.string.privacy_sheet_details),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

}



