package macc.AR.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.data.manager.LocalUserManagerImpl
import macc.AR.data.manager.SettingsManagerImpl
import macc.AR.domain.manager.AuthManager
import macc.AR.domain.manager.LocalUserManager
import macc.AR.domain.manager.SettingsManager
import macc.AR.domain.usecase.appEntry.AppEntryUseCases
import macc.AR.domain.usecase.appEntry.ReadAppEntry
import macc.AR.domain.usecase.appEntry.SaveAppEntry
import macc.AR.domain.usecase.auth.AuthCheck
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.domain.usecase.auth.Confirm
import macc.AR.domain.usecase.auth.SendEmail
import macc.AR.domain.usecase.auth.SignIn
import macc.AR.domain.usecase.auth.SignOut
import macc.AR.domain.usecase.auth.SignUp
import macc.AR.domain.usecase.auth.Subscribe
import macc.AR.domain.usecase.settings.FetchUserProfile
import macc.AR.domain.usecase.settings.SettingsUseCases
import macc.AR.domain.usecase.settings.Update
import javax.inject.Singleton

/*
* This module provides instances of class, it allow classes to depend on abstractions rather
* than concrete implementations, making the code more modular and maintainable.
* In order to inject a class into another file you need to use @Inject and the name of the class.
* Then Hilt takes care of providing the instance for you*/
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalUserManager(
        application: Application
    ): LocalUserManager = LocalUserManagerImpl(context = application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManager: LocalUserManager
    ) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager)
    )

    @Provides
    @Singleton
    fun provideAuthManager(): AuthManager = AuthManagerImpl()


    @Provides
    @Singleton
    fun provideAuthUseCases(
        authManager: AuthManager
    ) = AuthenticationUseCases(
        signIn = SignIn(authManager),
        signUp = SignUp(authManager),
        signOut = SignOut(authManager),
        confirm= Confirm(authManager),
        sendEmail=SendEmail(authManager),
        authCheck=AuthCheck(authManager),
        subscribe = Subscribe(authManager)
    )

    @Provides
    @Singleton
    fun provideSettingsManager(): SettingsManager = SettingsManagerImpl()


    @Provides
    @Singleton
    fun provideSettingsUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager
    ) = SettingsUseCases(
        update= Update(settingsManager),
        fetch= FetchUserProfile(settingsManager),
        subscribe = macc.AR.domain.usecase.settings.Subscribe(settingsManager)
    )
}