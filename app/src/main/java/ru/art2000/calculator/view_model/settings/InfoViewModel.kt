package ru.art2000.calculator.view_model.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.R
import ru.art2000.calculator.model.settings.AuthorLink
import ru.art2000.extensions.context
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class InfoViewModel(application: Application) : AndroidViewModel(application) {

    val authorLinks = listOf(
            AuthorLink(R.drawable.github, R.string.link_url_github),
            AuthorLink(R.drawable.gitlab, R.string.link_url_gitlab),
            AuthorLink(R.drawable.kde, R.string.link_url_kde),
    )

    fun getChangeLogText(): String? {
        return try {
            val stream: InputStream = context.resources.openRawResource(R.raw.changelog)

            val sb = StringBuilder()

            BufferedReader(InputStreamReader(stream)).use { reader ->
                var mLine: String?
                var i = 0
                while (reader.readLine().also { mLine = it } != null) {
                    if (i == 0) sb.append(mLine) else sb.append("\n").append(mLine)
                    i++
                }
            }

            sb.toString()
        } catch (_: Exception) {
            null
        }
    }
}