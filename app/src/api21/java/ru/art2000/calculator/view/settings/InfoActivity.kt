package ru.art2000.calculator.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.R
import ru.art2000.calculator.compose.*
import ru.art2000.calculator.model.settings.AuthorLink

class InfoActivity : InfoActivityBase() {

    public override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        if (BuildConfig.USE_COMPOSE) {
            setupCompose()
        } else {
            setupClassic()
        }
    }

    //Compose version

    private fun setupCompose() {
        setContent {
            AutoThemed {
                InfoScreenRoot()
            }
        }
    }

    @Preview
    @Composable
    private fun InfoScreenRoot() {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            val changelogBackgroundColor =
                getColorFromAttribute(R.attr.calculatorInputBackground)

            CalculatorAppBar(titleText = getString(R.string.info), onBackPressed = { finish() })

            SectionHeader(stringRes = R.string.changelog, marginBottom = 8.dp)
            ChangeLogText(
                text = model.getChangeLogText() ?: getString(R.string.changelog_load_failed),
                backgroundColor = changelogBackgroundColor,
            )

            SectionHeader(stringRes = R.string.dev)
            DevAvatar()
            DevLinks(links = model.authorLinks)
        }
    }

    @Composable
    private fun Context.SectionHeader(@StringRes stringRes: Int, marginBottom: Dp = 0.dp) {

        val accentColor = getColorFromAttribute(R.attr.colorAccent)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = marginBottom),
        ) {
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, top = 3.dp, end = 5.dp),
                color = accentColor
            )
            Text(
                text = getString(stringRes),
                color = accentColor,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(getColorFromAttribute(android.R.attr.windowBackground))
                    .padding(start = 5.dp, end = 5.dp)
            )
        }
    }

    @Composable
    private fun ColumnScope.ChangeLogText(text: String, backgroundColor: Color) {
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1.0f)
                .padding(6.dp)
                .verticalScroll(scrollState)
                .verticalScrollBar(scrollState)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp)),
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxSize(),
                style = TextStyle(
                    color = getColorFromAttribute(R.attr.colorOnSurface),
                    fontSize = 14.sp,
                    letterSpacing = 0.15.sp
                ),
            )
        }
    }

    @Composable
    private fun DevAvatar() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(top = 10.dp) // margin
                .border(
                    1.dp,
                    getColorFromAttribute(R.attr.strokeColor),
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp) // padding
        ) {
            Image(
                painter = attributeDrawablePainter(
                    attrRes = R.attr.authorAvatar,
                    defaultId = R.drawable.dev_avatar_darker,
                ),
                contentDescription = "dev avatar",
                modifier = Modifier.size(60.dp),
            )
            Text(
                text = getString(R.string.author_nick),
                fontSize = 20.sp,
                color = getColorFromAttribute(R.attr.colorOnSurface)
            )
        }
    }

    @Composable
    private fun DevLinks(links: Collection<AuthorLink>) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, 15.dp), Arrangement.SpaceAround
        ) {
            links.forEach {
                IconButton(onClick = {
                    val url = getString(it.link)
                    val githubIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(githubIntent)
                }, modifier = Modifier.size(40.dp)) {
                    Image(
                        painter = painterResource(id = it.image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}