package com.example.greenpantry.ui.sharedcomponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.greenpantry.ui.theme.green_primary
import com.example.greenpantry.ui.theme.light_green_background

@Composable
fun HeaderBackground(
    leftColor: Color,
    rightColor: Color,
    modifier: Modifier = Modifier
) {
    val colorList = remember {
        listOf(leftColor,rightColor)
    }

    Canvas(
        modifier = modifier
    ){
        drawCircle(
            radius = size.width * 13/12,
            center = Offset(center.x, -size.width/1.5f),
            brush = Brush.linearGradient(colorList, end = Offset(center.x+500f,0f))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderBackgroundPreview() {
    HeaderBackground(
        leftColor = green_primary,
        rightColor = light_green_background,
        modifier = Modifier
            .fillMaxSize()
    )
}