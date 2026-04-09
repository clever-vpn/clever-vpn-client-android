package com.clevervpn.app.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clevervpn.app.R

@Preview
@Composable
fun Logo(sizeRate: Float = 0.2f) {
    Icon(
        painterResource(id = R.drawable.logo_small_red),
        contentDescription = stringResource(R.string.logo_content_description),
        tint = Color.Unspecified,
        modifier = Modifier
            .fillMaxHeight(sizeRate)
            .sizeIn(maxWidth = 120.dp, maxHeight = 120.dp)
            .aspectRatio(1f)
//            .background(
//                shape = RoundedCornerShape(10.dp),
//                color = Color.Black
//            )
    )
}
