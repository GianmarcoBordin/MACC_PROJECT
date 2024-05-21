package com.mygdx.game.di

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.mygdx.game.data.api.DataRepositoryImpl
import com.mygdx.game.domain.api.MapApi
import com.mygdx.game.data.api.MapRepositoryImpl
import com.mygdx.game.domain.api.RankApi
import com.mygdx.game.data.dao.Biometric
import com.mygdx.game.data.manager.ARManagerImpl
import com.mygdx.game.data.manager.AuthManagerImpl
import com.mygdx.game.data.manager.ContextManagerImpl
import com.mygdx.game.data.manager.InventoryManagerImpl
import com.mygdx.game.data.manager.LocalUserManagerImpl
import com.mygdx.game.data.manager.MapManagerImpl
import com.mygdx.game.data.manager.RankManagerImpl
import com.mygdx.game.data.manager.SettingsManagerImpl
import com.mygdx.game.domain.api.DataRepository
import com.mygdx.game.domain.manager.ARManager
import macc.ar.domain.api.MapRepository
import com.mygdx.game.domain.manager.AuthManager
import com.mygdx.game.domain.manager.ContextManager
import com.mygdx.game.domain.manager.InventoryManager
import com.mygdx.game.domain.manager.LocalUserManager
import com.mygdx.game.domain.manager.MapManager
import com.mygdx.game.domain.manager.RankManager
import com.mygdx.game.domain.manager.SettingsManager
import com.mygdx.game.domain.usecase.Subscribe
import com.mygdx.game.domain.usecase.appEntry.AppEntryUseCases
import com.mygdx.game.domain.usecase.appEntry.ReadAppEntry
import com.mygdx.game.domain.usecase.appEntry.ReadSkin
import com.mygdx.game.domain.usecase.appEntry.ReadUser
import com.mygdx.game.domain.usecase.appEntry.SaveAppEntry
import com.mygdx.game.domain.usecase.appEntry.SaveUser
import com.mygdx.game.domain.usecase.ar.ARUseCases
import com.mygdx.game.domain.usecase.ar.AddGameItem
import com.mygdx.game.domain.usecase.ar.GetGameItem
import com.mygdx.game.domain.usecase.ar.SaveSkin
import com.mygdx.game.domain.usecase.inventory.GetGameItemsUser
import com.mygdx.game.domain.usecase.auth.AuthCheck
import com.mygdx.game.domain.usecase.auth.AuthenticationUseCases
import com.mygdx.game.domain.usecase.auth.BioSignIn
import com.mygdx.game.domain.usecase.auth.SignIn
import com.mygdx.game.domain.usecase.auth.SignUp
import com.mygdx.game.domain.usecase.home.HomeUseCases
import com.mygdx.game.domain.usecase.inventory.InventoryUseCases
import com.mygdx.game.domain.usecase.map.FetchUserLocation
import com.mygdx.game.domain.usecase.map.GetContext
import com.mygdx.game.domain.usecase.map.GetNearbyObjects
import com.mygdx.game.domain.usecase.map.GetNearbyPlayers
import com.mygdx.game.domain.usecase.map.GetRoute
import com.mygdx.game.domain.usecase.map.MapUseCases
import com.mygdx.game.domain.usecase.map.StartLocUpdates
import com.mygdx.game.domain.usecase.map.StopLocUpdates
import com.mygdx.game.domain.usecase.map.UpdateItemLocation
import com.mygdx.game.domain.usecase.map.UpdatePlayerLocation
import com.mygdx.game.domain.usecase.map.UpdateUserLocation
import com.mygdx.game.domain.usecase.rank.Fetch
import com.mygdx.game.domain.usecase.rank.RankUseCases
import com.mygdx.game.domain.usecase.settings.FetchUserProfile
import com.mygdx.game.domain.usecase.settings.SettingsUseCases
import com.mygdx.game.domain.usecase.settings.SignOut
import com.mygdx.game.domain.usecase.settings.Update
import com.mygdx.game.framework.LocationHandler
import com.mygdx.game.util.Constants.RANK_URL
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
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
    @Provides
    @Singleton
    fun provideBiometricState(): Biometric {
        return Biometric("","")
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RANK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideLocationHandler(context: Context): LocationHandler {
        return LocationHandler(context)
    }

    @Provides
    @Singleton
    fun provideRankApi(retrofit: Retrofit): RankApi {
        return retrofit.create(RankApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMapApi(retrofit: Retrofit): MapApi {
        return retrofit.create(MapApi::class.java)
    }


    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun provideDataRepository(rankApi: RankApi): DataRepository = DataRepositoryImpl(rankApi)

    @Provides
    @Singleton
    fun provideMapRepository(mapApi: MapApi, firestore: FirebaseFirestore, localUserManager: LocalUserManager,dataRepository: DataRepository): MapRepository = MapRepositoryImpl(
        mapApi,
        firestore,
        localUserManager,
        dataRepository
    )


    @Provides
    @Singleton
    fun provideLocalUserManager(
        application: Application,
        locationHandler: LocationHandler,
        firestore: FirebaseFirestore
    ): LocalUserManager = LocalUserManagerImpl(context = application, locationHandler = locationHandler,firestore )

    @Provides
    @Singleton
    fun provideAuthManager(firebaseAuth: FirebaseAuth,
                           dataRepository: DataRepository,
                           localUserManager: LocalUserManager,
                           firestore: FirebaseFirestore
    ): AuthManager = AuthManagerImpl(
         firebaseAuth =firebaseAuth, firestore = firestore, dataRepository = dataRepository, localUserManager = localUserManager )

    @Provides
    @Singleton
    fun provideSettingsManager(firebaseAuth: FirebaseAuth,
                               localUserManager: LocalUserManager,
                               authManager: AuthManager
    ): SettingsManager = SettingsManagerImpl(
        firebaseAuth =firebaseAuth, localUserManager = localUserManager, authManager = authManager
    )

    @Provides
    @Singleton
    fun provideContextManager(context: Context): ContextManager = ContextManagerImpl(context = context )
    @Provides
    @Singleton
    fun provideRankManager(dataRepository: DataRepository): RankManager = RankManagerImpl(dataRepository = dataRepository)

    @Provides
    @Singleton
    fun provideMapManager(mapRepository: MapRepository,localUserManager: LocalUserManager): MapManager = MapManagerImpl(mapRepository = mapRepository,localUserManager)

    @Provides
    @Singleton
    fun provideARManager(dataRepository: DataRepository, localUserManager: LocalUserManager): ARManager = ARManagerImpl(dataRepository = dataRepository, localUserManager = localUserManager)

    @Provides
    @Singleton
    fun provideInventoryManager(dataRepository: DataRepository): InventoryManager = InventoryManagerImpl(dataRepository = dataRepository)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManager: LocalUserManager,
        rankManager: RankManager
    ) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager),
        readUser = ReadUser(localUserManager),
        saveUser = SaveUser(localUserManager),
        readSkin = ReadSkin(rankManager)
    )
    @Provides
    @Singleton
    fun provideAuthUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager: RankManager,
        localUserManager: LocalUserManager,

    ) = AuthenticationUseCases(
        signIn = SignIn(authManager),
        signUp = SignUp(authManager),
        authCheck= AuthCheck(authManager),
        bioSignIn= BioSignIn(authManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager,localUserManager)

    )

    @Provides
    @Singleton
    fun provideSettingsUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager: RankManager,
        localUserManager: LocalUserManager,

    ) = SettingsUseCases(
        update= Update(settingsManager),
        fetch= FetchUserProfile(settingsManager),
        signOut = SignOut(settingsManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager,localUserManager)
    )

    @Provides
    @Singleton
    fun provideRankUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager: RankManager,
        localUserManager: LocalUserManager,


    ) = RankUseCases(
        fetch= Fetch(rankManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager,localUserManager)
    )

    @Provides
    @Singleton
    fun provideMapUseCases(
        settingsManager: SettingsManager,
        authManager: AuthManager,
        rankManager: RankManager,
        mapManager: MapManager,
        localUserManager: LocalUserManager,
        contextManager: ContextManager,
        inventoryManager: InventoryManager


    ) = MapUseCases(
        fetchUserLocation = FetchUserLocation(mapManager),
        subscribe = Subscribe(settingsManager,authManager, rankManager,localUserManager),
        getNearbyPlayers = GetNearbyPlayers(mapManager),
        getNearbyObjects = GetNearbyObjects(mapManager),
        getRoute = GetRoute(mapManager),
        startLocUpdates = StartLocUpdates(mapManager) ,
        stopLocUpdates = StopLocUpdates(mapManager),
        updateItemLocation = UpdateItemLocation(mapManager),
        updatePlayerLocation = UpdatePlayerLocation(mapManager),
        updateUserLocation = UpdateUserLocation(rankManager),
        getContext = GetContext(contextManager),
        readUser = ReadUser(localUserManager),
        getGameItemsUser = GetGameItemsUser(inventoryManager)
    )

    @Provides
    @Singleton
    fun provideARUseCases(
        arManager: ARManager,
        localUserManager: LocalUserManager,
    ) = ARUseCases(
        addGameItem = AddGameItem(arManager),
        getGameItem = GetGameItem(arManager),
        fetchUserProfile = ReadUser(localUserManager),
        saveSkin = SaveSkin(arManager)
    )

    @Provides
    @Singleton
    fun provideInventoryUseCases(
        inventoryManager: InventoryManager,
        localUserManager: LocalUserManager
    ) = InventoryUseCases(
        getGameItemsUser = GetGameItemsUser(inventoryManager) ,
        fetchUserProfile = ReadUser(localUserManager)
    )

    @Provides
    @Singleton
    fun provideHomeUseCases(
        inventoryManager: InventoryManager,
    ) = HomeUseCases(
        getGameItemsUser = GetGameItemsUser(inventoryManager)
    )

}