package ru.art2000.calculator.view.settings

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Space
import androidx.activity.viewModels
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityAppInfoBinding
import ru.art2000.calculator.databinding.AuthorLinkItemBinding
import ru.art2000.calculator.model.settings.AuthorLink
import ru.art2000.calculator.view_model.settings.InfoViewModel
import ru.art2000.extensions.activities.AutoThemeActivity

sealed class InfoActivityBase : AutoThemeActivity() {

    protected val model by viewModels<InfoViewModel>()

    protected fun setupClassic() {
        val binding = ActivityAppInfoBinding.inflate(layoutInflater)

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

    protected fun onLinkClick(link: AuthorLink) {
        val url = getString(link.link)
        val uriIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(uriIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}