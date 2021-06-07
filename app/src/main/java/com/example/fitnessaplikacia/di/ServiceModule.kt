package com.example.fitnessaplikacia.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fitnessaplikacia.ui.MainActivity
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) = NotificationManagerCompat.from(context)


    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) =
        NotificationCompat.Builder(
            context,
            Constants.NOTIFICATION_CHANNEL_ID
        )
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.running_man)
            .setContentTitle("Fitness")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)


    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext context: Context) =
        PendingIntent.getActivity(
            context,
            143,
            Intent(context, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                this.action = Constants.ACTION_SHOW_RUN_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}