package com.mygdx.game.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.mygdx.game.presentation.Dimension.IndicatorSize
import com.mygdx.game.presentation.Dimension.PageIndicatorPadding

/*
* Composable that displays current page on boarding*/
@Composable
fun PagerIndicator (
    pageSize: Int,
    selectedPage: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurface
){
    Row (modifier = Modifier, horizontalArrangement = Arrangement.SpaceBetween){
        repeat(times = pageSize) { page ->
            Box(
                modifier = Modifier
                    .size(IndicatorSize)
                    .clip(CircleShape)
                    .background(color = if (page == selectedPage) selectedColor else unselectedColor)
            )
            Spacer(modifier = Modifier.width(PageIndicatorPadding))
        }
    }
}