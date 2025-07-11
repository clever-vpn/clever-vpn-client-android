package com.clevervpn.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FlagByCountryCode(countryCode: String? = null) {
    var flag: String
    if (countryCode.isNullOrEmpty()) {
        flag = "\uD83C\uDF10"
    }else {
        flag = countryCode
            .uppercase()
            .map { char -> 0x1F1E6 + (char.code - 'A'.code) }
            .joinToString("") { code -> String(Character.toChars(code)) }
    }
    Text(
        text = flag,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(8.dp)
    )
}

// 使用示例
@Preview
@Composable
fun PreviewFlag() {
    // 显示不同国家的国旗
    Column {
        FlagByCountryCode("") // 显示 🇺🇸
        FlagByCountryCode("CN") // 显示 🇨🇳
        FlagByCountryCode("FR") // 显示 🇫🇷
        FlagByCountryCode("JP") // 显示 🇯🇵
    }

}