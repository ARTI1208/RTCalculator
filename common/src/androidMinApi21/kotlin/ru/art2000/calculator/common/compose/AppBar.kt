package ru.art2000.calculator.common.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.art2000.extensions.getDrawableResFromAttribute
import androidx.appcompat.R as AppcompatR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorAppBar(
    titleText: String,
    onBackPressed: (() -> Unit)? = null,
) {

    val color = MaterialTheme.colorScheme.onSurface

    TopAppBar(
        title = {
            Text(text = titleText, color = color)
        }, navigationIcon = {
            if (onBackPressed != null) {
                IconButton(onClick = onBackPressed) {
                    Image(
                        painter = painterResource(
                            LocalContext.current
                                .getDrawableResFromAttribute(AppcompatR.attr.homeAsUpIndicator)
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(color),
                    )
                }
            }
        }
    )

}