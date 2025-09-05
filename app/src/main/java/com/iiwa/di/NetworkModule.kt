/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Network dependency injection module for API and HTTP clients
 */

package com.iiwa.di

import android.content.Context
import com.iiwa.authorization.repository.UserRepository
import com.iiwa.data.api.ApiConfig
import com.iiwa.data.api.ApiService
import com.iiwa.data.api.AuthApi
import com.iiwa.data.api.ServiceGenerator
import com.iiwa.data.local.AppDatabase
import com.iiwa.data.local.TokenStorage
import com.iiwa.data.local.UserDao
import com.iiwa.data.remote.RemoteData
import com.iiwa.utils.Network
import com.iiwa.utils.NetworkConnectivity
import com.iiwa.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ServiceGenerator.createService(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        return ServiceGenerator.createService(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideServiceGenerator(): ServiceGenerator {
        return ServiceGenerator
    }

    @Provides
    @Singleton
    fun provideApiConfig(@ApplicationContext context: Context): ApiConfig {
        return ApiConfig(context)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity {
        return Network(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return TokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideRemoteData(networkConnectivity: NetworkConnectivity): RemoteData {
        return RemoteData(networkConnectivity)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        authApi: AuthApi,
        tokenStorage: TokenStorage,
        remoteData: RemoteData,
        userDao: UserDao,
    ): UserRepository {
        return UserRepository(authApi, tokenStorage, remoteData, userDao)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }
}
