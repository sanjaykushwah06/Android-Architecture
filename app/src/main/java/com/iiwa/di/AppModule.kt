/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Dependency injection module for app-wide dependencies and providers
 */

package com.iiwa.di

import android.content.Context
import com.iiwa.pushnotification.NotificationHelper
import com.iiwa.pushnotification.NotificationPermissionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepository(): MyRepository = MyRepositoryImpl()
    
    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationPermissionHelper(@ApplicationContext context: Context): NotificationPermissionHelper {
        return NotificationPermissionHelper(context)
    }
}

interface MyRepository {
    fun getMessage(): String
}

class MyRepositoryImpl : MyRepository {
    override fun getMessage() = "Data from Repository via Hilt"
}
