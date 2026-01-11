package com.example.abhiyant.di

import com.example.abhiyant.data.dao.InspectionDao
import com.example.abhiyant.data.repository.InspectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideInspectionRepository(
        inspectionDao: InspectionDao
    ): InspectionRepository {
        return InspectionRepository(inspectionDao)
    }
}

