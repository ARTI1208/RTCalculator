package ru.art2000.calculator.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.art2000.calculator.R

@Composable
fun CalculatorAppBar(
    titleText: String,
    onBackPressed: (() -> Unit)? = null,
    leadButtonRippleRadius: Dp = 20.dp,
) {

    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        elevation = dimensionResource(R.dimen.actionbar_elevation),
    ){

        if (onBackPressed != null) {
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = onBackPressed,
                        enabled = true,
                        role = Role.Button,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = leadButtonRippleRadius
                        )
                    )
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                val contentAlpha = LocalContentAlpha.current
                CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = {
                    Image(
                        painter = painterResource(R.drawable.ic_ab_back),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                    )
                })
            }
        }


        Row(
            Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(start = getTitleMargin().dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.h6) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                    content = { Text(text = titleText, color = MaterialTheme.colors.secondary) }
                )
            }
        }
    }

}

// TODO: implement correctly
@Composable
private fun getTitleMargin(): Float {
    return 7.5f
}