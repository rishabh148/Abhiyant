package com.example.abhiyant.di

import android.content.Context
import androidx.room.Room
import com.example.abhiyant.data.dao.InspectionDao
import com.example.abhiyant.data.database.InspectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideInspectionDatabase(
        @ApplicationContext context: Context
    ): InspectionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            InspectionDatabase::class.java,
            "inspection_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideInspectionDao(database: InspectionDatabase): InspectionDao {
        return database.inspectionDao()
    }
}

