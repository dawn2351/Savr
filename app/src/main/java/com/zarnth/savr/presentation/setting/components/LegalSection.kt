package com.zarnth.savr.presentation.setting.components

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zarnth.savr.R
import com.zarnth.savr.openChromeTab

@Composable
fun LegalSection(context: Context) {
    Spacer(Modifier.height(12.dp))
    SectionHeader("Legal")
    SettingItem(
        icon = R.drawable.privacy_policy_icon,
        title = "Privacy Policy",
        onClick = { openChromeTab("https://soapy-background-145.notion.site/Privacy-Policy-of-Savr-3833b17db1b380d69dbaf9cfb8a14738?pvs=73", context) }
    )
    Spacer(Modifier.height(4.dp))
    SettingItem(
        icon = R.drawable.terms_icons,
        title = "Terms & Conditions",
        onClick = { openChromeTab("https://soapy-background-145.notion.site/Terms-Conditions-of-Savr-3953b17db1b38035bc8ac3dd35dda1cc?pvs=73", context) }
    )
}
