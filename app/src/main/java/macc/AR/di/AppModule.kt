package macc.AR.di

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import macc.AR.data.BiometricState
import macc.AR.data.api.DataRepositoryImpl
import macc.AR.data.api.RankApi
import macc.AR.data.manager.AuthManagerImpl
import macc.AR.data.manager.LocalUserManagerImpl
import macc.AR.data.manager.RankManagerImpl
import macc.AR.data.manager.SettingsManagerImpl
import macc.AR.domain.api.DataRepository
import macc.AR.domain.manager.AuthManager
import macc.AR.domain.manager.LocalUserManager
import macc.AR.domain.manager.RankManager
import macc.AR.domain.manager.SettingsManager
import macc.AR.domain.usecase.Subscribe
import macc.AR.domain.usecase.appEntry.AppEntryUseCases
import macc.AR.domain.usecase.appEntry.ReadAppEntry
import macc.AR.domain.usecase.appEntry.SaveAppEntry
import macc.AR.domain.usecase.auth.AuthCheck
import macc.AR.domain.usecase.auth.AuthenticationUseCases
import macc.AR.domain.usecase.auth.BioSignIn
import macc.AR.domain.usecase.auth.SignIn
import macc.AR.domain.usecase.auth.SignUp
import macc.AR.domain.usecase.rank.Fetch
import macc.AR.domain.usecase.rank.RankUseCases
import macc.AR.domain.usecase.settings.FetchUserProfile
import macc.AR.domain.usecase.settings.SettingsUseCases
import macc.AR.domain.usecase.settings.SignOut
import macc.AR.domain.usecase.settings.Update
import macc.AR.util.Constants.RANK_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideBiometricState(): BiometricState {
        return BiometricState("","")
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RANK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRankApi(retrofit: Retrofit): RankApi {
        return retrofit.create(RankApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDataRepository(rankApi: RankApi): DataRepository = DataRepositoryImpl(rankApi)


    @Provides
    @Singleton
    fun provideLocalUserManager(
        application: Application
    ): LocalUserManager = LocalUserManagerImpl(context = application)


    @Provides
    @Singleton
    fun provideAuthManager(firebaseAuth: FirebaseAuth,
                           biometricState: BiometricState): AuthManager = AuthManagerImpl(
        biometricState =biometricState , firebaseAuth =firebaseAuth)


    @Provides
    @Singleton
    fun provideSettingsManager(firebaseAuth: FirebaseAuth,
                           biometricState: BiometricState): SettingsManager = SettingsManagerImpl(
        biometricState =biometricState , firebaseAuth =firebaseAuth)

    @Provides
    @Singleton
    fun provideRankManager(dataRepository: DataRepository): RankManager = RankManagerImpl(dataRepository = dataRepository)


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
    fun provideAuthUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager:RankManager
    ) = AuthenticationUseCases(
        signIn = SignIn(authManager),
        signUp = SignUp(authManager),
        authCheck=AuthCheck(authManager),
        bioSignIn= BioSignIn(authManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager)

    )

    @Provides
    @Singleton
    fun provideSettingsUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager:RankManager
    ) = SettingsUseCases(
        update= Update(settingsManager),
        fetch= FetchUserProfile(settingsManager),
        signOut = SignOut(settingsManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager)
    )

    @Provides
    @Singleton
    fun provideRankUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager:RankManager
    ) = RankUseCases(
        fetch= Fetch(rankManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager)
    )

}