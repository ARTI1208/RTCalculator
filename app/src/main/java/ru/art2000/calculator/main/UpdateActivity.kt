package ru.art2000.calculator.main

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.databinding.ActivityUpdateBinding

class UpdateActivity : AppActivity() {

    private val binding by viewBinding<ActivityUpdateBinding>(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.downloadUpdateButton.setOnClickListener {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                "https://github.com/ARTI1208/RTCalculator/releases?q=v2".toUri()
            ))
        }
    }

}