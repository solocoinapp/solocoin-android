# Welcome to Solocoin!

The SoloCoin, it's great to have you here! We thank you in advance for your contributions. SoloCoin gamifies the act of social-distancing by rewarding users in points and badges for staying at home and away from people.

Initial development is started on `dev` branch after releasing beta version. Further development is going on `dev-v2` branch. MVVM architecture is used for structuring `dev-v2` branch with Kotlin and Koin integration for dependency injection.

## Project structure

```
app.solocoin.solocoin    	# Root Package
    .
    ├── app 				# For app level classes
    ├── repo                # Repository to handle data from network using API.
    ├── model               # Model classes
    ├── di                  # Dependency Injection     
    |
    ├── ui                  # Activity/View layer  
    |
    ├── utils               # Utility Classes / Kotlin extensions
    └── worker              # Worker class.
```

## Libraries used
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
- [Koin](https://start.insert-koin.io/) - Dependency Injection Framework (Kotlin)
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
- [Gson](https://github.com/google/gson) - A modern JSON library for Kotlin and Java.
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - The WorkManager API makes it easy to schedule deferrable, asynchronous tasks that are expected to run even if the app exits or device restarts.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android

## Contribution
Connect on [Discord](https://discord.gg/9Cegpv), ask @arbob to get started with contribution