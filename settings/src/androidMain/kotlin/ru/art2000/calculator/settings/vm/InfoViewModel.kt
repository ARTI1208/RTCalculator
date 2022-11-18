package ru.art2000.calculator.settings.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.settings.R
import ru.art2000.calculator.settings.model.AuthorLink
import ru.art2000.extensions.arch.context
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

internal class InfoViewModel(application: Application) : AndroidViewModel(application), IInfoViewModel {

    override val authorLinks = listOf(
        AuthorLink(R.drawable.github, R.string.link_url_github),
        AuthorLink(R.drawable.gitlab, R.string.link_url_gitlab),
        AuthorLink(R.drawable.kde, R.string.link_url_kde),
    )

    override val changeLogText: String?
        get() {
            return try {
                val stream: InputStream = context.resources.openRawResource(R.raw.changelog)

                val sb = StringBuilder()

                BufferedReader(InputStreamReader(stream)).use { reader ->
                    var mLine: String?
                    var firstLine = true
                    while (reader.readLine().also { mLine = it } != null) {
                        if (firstLine) sb.append(mLine) else sb.append("\n").append(mLine)
                        firstLine = false
                    }
                }

                sb.toString()
            } catch (_: Exception) {
                null
            }
        }
}