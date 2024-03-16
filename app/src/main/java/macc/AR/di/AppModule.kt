package macc.AR.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import macc.AR.data.manager.LocalUserManagerImpl
import macc.AR.domain.manager.LocalUserManager
import macc.AR.domain.usecase.AppEntryUseCases
import macc.AR.domain.usecase.ReadAppEntry
import macc.AR.domain.usecase.SaveAppEntry
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
}