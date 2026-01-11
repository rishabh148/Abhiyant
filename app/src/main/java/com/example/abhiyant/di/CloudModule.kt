package com.example.abhiyant.di

import com.example.abhiyant.data.cloud.CloudStorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudModule {
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    @Provides
    @Singleton
    fun provideCloudStorageService(
        firestore: FirebaseFirestore,
        gson: Gson
    ): CloudStorageService {
        return CloudStorageService(firestore, gson)
    }
}

