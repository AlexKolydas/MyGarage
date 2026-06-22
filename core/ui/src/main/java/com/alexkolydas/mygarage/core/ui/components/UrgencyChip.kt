package com.alexkolydas.mygarage.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexkolydas.mygarage.core.ui.model.UrgencyUi

@Composable
fun UrgencyChip(urgency: UrgencyUi, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(urgency.bg)
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(urgency.fg))
        Text(urgency.label, color = urgency.fg, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ServiceStatusChip(urgency: UrgencyUi, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(urgency.bg)
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Text(urgency.label, color = urgency.fg, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
    }
}
