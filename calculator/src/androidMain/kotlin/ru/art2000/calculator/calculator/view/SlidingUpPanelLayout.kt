package ru.art2000.calculator.calculator.view

import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout

var SlidingUpPanelLayout.state: PanelState
    get() = panelState
    set(value) = setPanelState(value)