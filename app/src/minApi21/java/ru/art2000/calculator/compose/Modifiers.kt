package ru.art2000.calculator.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.material.R as MaterialR

fun Modifier.verticalScrollBar(
    state: ScrollState,
    color: Color? = null,
    width: Dp = 4.dp,
    scrollingDuration: Int = 150,
    idleDuration: Int = 500,
    minHeightPx: Float? = null,
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) scrollingDuration else idleDuration

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    val actualColor =
        color ?: LocalContext.current.getColorFromAttribute(MaterialR.attr.colorControlNormal)

    drawWithContent {
        drawContent()

        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar) {
            val containerHeight = size.height
            val visibleHeight = containerHeight - state.maxValue

            val minScrollBarHeight = minHeightPx ?: 20.dp.toPx()
            val scrollbarHeight =
                ((visibleHeight / containerHeight) * visibleHeight).coerceAtLeast(minScrollBarHeight)
            val scrollbarOffsetY =
                ((state.value.toFloat() / state.maxValue) * (visibleHeight - scrollbarHeight)) + state.value

            drawRect(
                color = actualColor,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}