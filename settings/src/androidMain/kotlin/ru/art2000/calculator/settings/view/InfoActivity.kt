package ru.art2000.calculator.settings.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.settings.R
import ru.art2000.calculator.settings.databinding.ActivityAppInfoBinding
import ru.art2000.calculator.settings.databinding.AuthorLinkItemBinding
import ru.art2000.calculator.settings.model.AuthorLink
import ru.art2000.calculator.settings.vm.InfoViewModel
import ru.art2000.extensions.views.isLandscape
import androidx.core.net.toUri

internal class InfoActivity : AppActivity() {

    private val model by viewModels<InfoViewModel>()
    private val binding by viewBinding<ActivityAppInfoBinding>(CreateMethod.INFLATE)

    override val topViews: List<View>
        get() = listOf(binding.appBarLayout)

    override val bottomViews: List<View>
        get() = when (isLandscape) {
            true -> listOf(binding.changelogScrollview, binding.linksBlock)
            false -> listOf(binding.root)
        }

    override val leftViews: List<View>
        get() = listOf(binding.root)

    override val rightViews: List<View>
        get() = listOf(binding.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.changelog.text = model.changeLogText ?: getString(R.string.changelog_load_failed)

        binding.linksBlock.post {
            addLinks(binding.linksBlock)
        }
    }

    private fun addLinks(linksRoot: ViewGroup) {
        val width = linksRoot.width
        val imageSize = resources.getDimensionPixelSize(R.dimen.author_info_link_image_size)
        val linksCount = model.authorLinks.size
        val gapSize = width / linksCount - imageSize
        repeat(linksCount) { i ->
            val link = model.authorLinks[i]
            val linkButton = AuthorLinkItemBinding.inflate(layoutInflater).root
            linkButton.setImageResource(link.image)
            linkButton.setOnClickListener {
                onLinkClick(link)
            }
            linksRoot.addView(linkButton, imageSize, imageSize)
            if (i < linksCount - 1) {
                val space = Space(this)
                linksRoot.addView(space, gapSize, 0)
            }
        }
    }

    private fun onLinkClick(link: AuthorLink) {
        val url = getString(link.link)
        val uriIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(uriIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}