package ru.art2000.calculator.main

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.R
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.databinding.ActivityUpdateBinding

class UpdateActivity : AppActivity(R.layout.activity_update) {

    private val binding by viewBinding(ActivityUpdateBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.downloadUpdateButton.setOnClickListener {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                "https://github.com/ARTI1208/RTCalculator/releases?q=v2".toUri()
            ))
        }
    }

}