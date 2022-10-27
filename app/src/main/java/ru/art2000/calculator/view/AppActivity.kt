package ru.art2000.calculator.view

import android.os.Bundle
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.activities.IEdgeToEdgeActivity

abstract class AppActivity : AutoThemeActivity(), IEdgeToEdgeActivity {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        applyEdgeToEdgeIfAvailable()
    }

}