package app.solocoin.solocoin.di

import app.solocoin.solocoin.ui.auth.LoginSignupViewModel
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
}