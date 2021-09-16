package ru.art2000.calculator.view.settings

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.R
import ru.art2000.calculator.compose.*
import ru.art2000.calculator.model.settings.AuthorLink
import ru.art2000.calculator.view_model.settings.IInfoViewModel

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
                InfoScreenRoot(model)
            }
        }
    }

    @Preview
    @Composable
    private fun LightThemePreview() {
        LightTheme {
            InfoScreenRoot(PreviewInfoViewModel)
        }
    }

    @Preview
    @Composable
    private fun DarkThemePreview() {
        DarkTheme {
            InfoScreenRoot(PreviewInfoViewModel)
        }
    }

    @Preview
    @Composable
    private fun BlackThemePreview() {
        BlackTheme {
            InfoScreenRoot(PreviewInfoViewModel)
        }
    }

    private object PreviewInfoViewModel : IInfoViewModel {

        override val authorLinks = listOf(
            AuthorLink(R.drawable.github, R.string.link_url_github),
            AuthorLink(R.drawable.gitlab, R.string.link_url_gitlab),
            AuthorLink(R.drawable.kde, R.string.link_url_kde),
        )

        private val line = """
            
            v1.0
             - Initial Release
             
        """.trimIndent()

        override val changeLogText = line.repeat(20)

    }

    @Composable
    private fun InfoScreenRoot(
        model: IInfoViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colors.background),
        ) {

            CalculatorAppBar(titleText = stringResource(R.string.info),
                onBackPressed = { finish() }
            )

            SectionHeader(stringRes = R.string.changelog, marginBottom = 8.dp)
            ChangeLogText(
                text = model.changeLogText ?: stringResource(R.string.changelog_load_failed),
                textColor = MaterialTheme.colors.onSurface,
                backgroundColor = MaterialTheme.calculatorColors.colorSurfaceVariant,
            )

            SectionHeader(stringRes = R.string.dev)
            DevAvatar()
            DevLinks(links = model.authorLinks)
        }
    }

    @Composable
    private fun SectionHeader(@StringRes stringRes: Int, marginBottom: Dp = 0.dp) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = marginBottom),
        ) {
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, top = 3.dp, end = 5.dp),
                color = MaterialTheme.colors.secondary,
            )
            Text(
                text = stringResource(stringRes),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .padding(start = 5.dp, end = 5.dp)
            )
        }
    }

    @Composable
    private fun ColumnScope.ChangeLogText(text: String, textColor: Color, backgroundColor: Color) {
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1.0f)
                .padding(dimensionResource(R.dimen.changelog_padding))
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
                    color = textColor,
                    fontSize = 14.sp,
                    letterSpacing = 0.15.sp
                ),
            )
        }
    }

    @Composable
    private fun DevAvatar() {
        val avatarPadding = dimensionResource(R.dimen.author_avatar_padding)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(top = avatarPadding) // margin
                .border(
                    1.dp,
                    MaterialTheme.calculatorColors.strokeColor,
                    RoundedCornerShape(10.dp)
                )
                .padding(avatarPadding) // padding
        ) {

            Image(
                painter = painterResource(R.drawable.dev_avatar),
                contentDescription = "Dev avatar",
                modifier = Modifier.size(dimensionResource(R.dimen.author_avatar_image_size)),
            )
            Text(
                text = stringResource(R.string.author_nick),
                fontSize = textUnitResource(R.dimen.author_nick_text_size, TextUnitType.Sp),
                color = MaterialTheme.colors.onSurface,
            )
        }
    }

    @Composable
    private fun DevLinks(links: Collection<AuthorLink>) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, dimensionResource(R.dimen.author_info_link_block_margin)),
            Arrangement.SpaceAround,
        ) {
            links.forEach {
                val url = stringResource(it.link)
                IconButton(onClick = {
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