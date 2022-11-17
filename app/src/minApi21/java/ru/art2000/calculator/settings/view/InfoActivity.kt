package ru.art2000.calculator.settings.view

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.composethemeadapter3.Mdc3Theme
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.R
import ru.art2000.calculator.common.compose.*
import ru.art2000.calculator.settings.model.AuthorLink
import ru.art2000.calculator.settings.vm.IInfoViewModel
import ru.art2000.extensions.activities.clearSystemBars
import ru.art2000.extensions.views.isDarkThemeApplied

class InfoActivity : InfoActivityBase() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.USE_COMPOSE) {
            setupCompose()
        } else {
            setupClassic()
        }
    }

    //Compose version

    private fun setupCompose() {
        if (isFullscreen) {
            clearSystemBars()
        }
        setContent {
            Mdc3Theme {
                InfoScreenRoot(model)
            }
        }
    }

    @Preview
    @Composable
    private fun LightThemePreview() {
        Mdc3Theme {
            InfoScreenRoot(PreviewInfoViewModel)
        }
    }

    @Preview
    @Composable
    private fun LightThemePreviewShort() {
        Mdc3Theme {
            InfoScreenRoot(PreviewInfoViewModelShort)
        }
    }

    @Preview(device = Devices.TABLET)
    @Composable
    private fun LightThemePreviewLandscape() {
        Mdc3Theme {
            InfoScreenRoot(PreviewInfoViewModel)
        }
    }

    @Preview(device = Devices.TABLET)
    @Composable
    private fun LightThemePreviewShortLandscape() {
        Mdc3Theme {
            InfoScreenRoot(PreviewInfoViewModelShort)
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

    private object PreviewInfoViewModelShort : IInfoViewModel {

        override val authorLinks = listOf(
            AuthorLink(R.drawable.github, R.string.link_url_github),
            AuthorLink(R.drawable.gitlab, R.string.link_url_gitlab),
            AuthorLink(R.drawable.kde, R.string.link_url_kde),
        )

        private val line = """
            
            v1.0
             - Initial Release
             
        """.trimIndent()

        override val changeLogText = line.repeat(2)

    }

    @Composable
    private fun InfoScreenRoot(
        model: IInfoViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {

            CalculatorAppBar(titleText = stringResource(R.string.info),
                onBackPressed = { finish() }
            )

            if (isLandscape) {
                HorizontalInfoContent(model = model)
            } else {
                VerticalInfoContent(model = model)
            }
        }
    }

    @Composable
    private fun ColumnScope.VerticalInfoContent(model: IInfoViewModel) {
        ChangelogLayout(model = model)
        DevLayout(model = model)
    }

    @Composable
    private fun HorizontalInfoContent(model: IInfoViewModel) {

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.navigationBarsPaddingIfNeeded { only(WindowInsetsSides.Horizontal) },
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ChangelogLayout(model = model)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                DevLayout(model = model)
            }
        }
    }

    @Composable
    private fun ColumnScope.ChangelogLayout(model: IInfoViewModel) {
        SectionHeader(stringRes = R.string.changelog, marginBottom = 8.dp)
        ChangeLogText(
            text = model.changeLogText ?: stringResource(R.string.changelog_load_failed),
            textColor = MaterialTheme.colorScheme.onSurface,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }

    @Composable
    private fun ColumnScope.DevLayout(model: IInfoViewModel) {
        SectionHeader(stringRes = R.string.dev)
        DevAvatar()
        DevLinks(links = model.authorLinks)
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
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(stringRes),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
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
                .verticalScroll(scrollState)
        ) {

            Box(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.changelog_padding))
                    .navigationBarsPaddingIfLandscape { only(WindowInsetsSides.Bottom) }
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
    }

    @Composable
    private fun ColumnScope.DevAvatar() {
        val avatarPadding = dimensionResource(R.dimen.author_avatar_padding)

        val modifier = if (isLandscape) Modifier.weight(1f) else Modifier

        Box(contentAlignment = Alignment.Center, modifier = modifier) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(top = avatarPadding) // margin
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(avatarPadding) // padding
            ) {

                CompositionLocalProvider(LocalContentAlpha provides LocalContentAlpha.current) {
                    Image(
                        painter = painterResource(R.drawable.dev_avatar),
                        contentDescription = "Dev avatar",
                        modifier = Modifier.size(dimensionResource(R.dimen.author_avatar_image_size)),
                    )
                }

                Text(
                    text = stringResource(R.string.author_nick),
                    fontSize = textUnitResource(R.dimen.author_nick_text_size, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

    @Composable
    private fun DevLinks(links: Collection<AuthorLink>) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(0.dp, dimensionResource(R.dimen.author_info_link_block_margin))
                .navigationBarsPaddingIfNeeded { only(WindowInsetsSides.Bottom) },
            Arrangement.SpaceAround,
        ) {
            links.forEach {
                IconButton(onClick = {
                    onLinkClick(it)
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

    private val isLandscape
        @Composable
        get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    private val isFullscreen: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isDarkThemeApplied)

    private fun Modifier.navigationBarsPaddingIfNeeded(foo: WindowInsets.() -> WindowInsets = { this }) = composed {
        if (isFullscreen) windowInsetsPadding(WindowInsets.navigationBars.foo()) else this
    }

    private fun Modifier.navigationBarsPaddingIfLandscape(foo: WindowInsets.() -> WindowInsets = { this }) = composed {
        if (isLandscape) navigationBarsPaddingIfNeeded(foo) else this
    }

}