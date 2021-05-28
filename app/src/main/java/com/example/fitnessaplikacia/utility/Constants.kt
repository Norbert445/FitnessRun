package com.example.fitnessaplikacia.utility

import android.graphics.Color

object Constants {

    // Shared preferences
    const val SHARED_PREF_NAME = "sharedPref"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"

    // Database ROOM
    const val DATABASE_NAME = "RUNNING_DATABASE"

    // Tracking Service
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val NOTIFICATION_CHANNEL_ID = "run_channel"
    const val NOTIFICATION_CHANNEL_NAME = "run_channel_name"
    const val NOTIFICATION_ID = 9
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

    // Pending Intent
    const val ACTION_SHOW_RUN_FRAGMENT = "ACTION_SHOW_RUN_FRAGMENT"

    // Runfragment
    const val POLYLINE_COLOR = Color.BLUE
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    // StartRunFragment
    const val REQUEST_CODE_LOCATION = 0
}