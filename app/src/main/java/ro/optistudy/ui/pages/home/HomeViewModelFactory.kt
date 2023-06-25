package ro.optistudy.ui.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ro.optistudy.ui.pages.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel() as T
    }
}