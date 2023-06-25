package ro.optistudy.ui.extensions

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED