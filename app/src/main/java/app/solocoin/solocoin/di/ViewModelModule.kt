package app.solocoin.solocoin.di

import app.solocoin.solocoin.ui.auth.CreateProfileViewModel
import app.solocoin.solocoin.ui.auth.LoginSignupViewModel
import app.solocoin.solocoin.ui.auth.MarkYourLocationViewModel
import app.solocoin.solocoin.ui.home.HomeFragmentViewModel
import app.solocoin.solocoin.ui.home.QuizViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel {
        LoginSignupViewModel(repository = get())
    }
    viewModel {
        MarkYourLocationViewModel(repository = get())
    }
    viewModel {
        CreateProfileViewModel(get())
    }
    viewModel {
        QuizViewModel(repository = get())
    }
    viewModel {
        HomeFragmentViewModel(get())
    }
}