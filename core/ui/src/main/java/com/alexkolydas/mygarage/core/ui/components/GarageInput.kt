package com.alexkolydas.mygarage.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexkolydas.mygarage.core.ui.theme.*

@Composable
fun SheetLabel(text: String) {
    Text(
        text = text,
        fontFamily = IbmPlexSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 12.sp,
        letterSpacing = 0.5.sp,
        color  = GarageTextSecond,
        modifier = Modifier.padding(bottom = 7.dp),
    )
}

@Composable
fun GarageInput(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    mono: Boolean = false,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text(
                text = placeholder,
                color = GarageTextSecond,
                fontSize = 15.sp,
                fontFamily = if (mono) IbmPlexMonoFamily else IbmPlexSansFamily,
            )
        },
        singleLine = true,
        modifier   = modifier.fillMaxWidth(),
        shape      = RoundedCornerShape(13.dp),
        colors     = garageInputColors(),
        textStyle  = TextStyle(
            color = GarageTextPrimary,
            fontSize = 15.sp,
            fontFamily = if (mono) IbmPlexMonoFamily else IbmPlexSansFamily,
        ),
    )
}

@Composable
fun GarageMultilineInput(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text(placeholder, color = GarageTextSecond, fontSize = 15.sp)
        },
        minLines  = 3,
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(13.dp),
        colors    = garageInputColors(),
        textStyle = TextStyle(color = GarageTextPrimary, fontSize = 15.sp, lineHeight = 22.sp),
    )
}

@Composable
fun garageInputColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = GarageInputBg,
    focusedContainerColor   = GarageInputBg,
    unfocusedBorderColor    = Color.White.copy(alpha = 0.08f),
    focusedBorderColor      = GarageAccent,
    cursorColor             = GarageAccent,
    unfocusedTextColor      = GarageTextPrimary,
    focusedTextColor        = GarageTextPrimary,
)
