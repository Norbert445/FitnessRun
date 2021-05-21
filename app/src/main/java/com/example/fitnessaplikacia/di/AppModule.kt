package com.example.fitnessaplikacia.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.fitnessaplikacia.db.RunningDatabase
import com.example.fitnessaplikacia.utility.Constants.DATABASE_NAME
import com.example.fitnessaplikacia.utility.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.fitnessaplikacia.utility.Constants.KEY_NAME
import com.example.fitnessaplikacia.utility.Constants.KEY_WEIGHT
import com.example.fitnessaplikacia.utility.Constants.SHARED_PREF_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app: Context): RunningDatabase =
        Room.databaseBuilder(
            app,
            RunningDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) =
        db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 85f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) = sharedPref.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true
    )
}