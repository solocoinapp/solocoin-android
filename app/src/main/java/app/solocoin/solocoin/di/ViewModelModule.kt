package app.solocoin.solocoin.di

import app.solocoin.solocoin.ui.auth.CreateProfileViewModel
import app.solocoin.solocoin.ui.auth.LoginSignupViewModel
import app.solocoin.solocoin.ui.auth.MarkYourLocationViewModel
import app.solocoin.solocoin.ui.home.*
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
        CreateProfileViewModel(repository = get())
    }
    viewModel {
        HomeFragmentViewModel(repository = get())
    }
    viewModel {
        WalletFragmentViewModel(repository = get())
    }
    viewModel {
        MilestonesFragmentViewModel(repository = get())
    }
    viewModel {
        QuizViewModel(repository = get())
    }
    viewModel {
        RewardRedeemViewModel(repository = get())
    }
    viewModel {
        AllScratchCardsViewModel(repository = get())
    }
}