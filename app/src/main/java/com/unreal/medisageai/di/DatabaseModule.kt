package com.unreal.medisageai.di

import android.content.Context
import androidx.room.Room
import com.unreal.medisageai.data.local.ChatDao
import com.unreal.medisageai.data.local.MediSageDatabase
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
    fun provideMediSageDatabase(
        @ApplicationContext context: Context,
    ): MediSageDatabase = Room.databaseBuilder(
        context,
        MediSageDatabase::class.java,
        MediSageDatabase.NAME,
    )
        // Development convenience: recreate the schema instead of writing
        // migrations. Replace with addMigrations(...) before shipping real data.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    @Singleton
    fun provideChatDao(database: MediSageDatabase): ChatDao = database.chatDao()
}
