package com.example.fitnessaplikacia.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.fitnessaplikacia.models.RunEvent
import com.example.fitnessaplikacia.utility.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_STOP_SERVICE
import com.example.fitnessaplikacia.utility.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.example.fitnessaplikacia.utility.Constants.LOCATION_UPDATE_INTERVAL
import com.example.fitnessaplikacia.utility.Constants.NOTIFICATION_CHANNEL_ID
import com.example.fitnessaplikacia.utility.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.fitnessaplikacia.utility.Constants.NOTIFICATION_ID
import com.example.fitnessaplikacia.utility.PermissionUtil
import com.example.fitnessaplikacia.utility.PolylineLengthUtil
import com.example.fitnessaplikacia.utility.TimerUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    @set:Inject
    var weight: Float = 80f

    companion object {
        val runEvent = MutableLiveData<RunEvent>()
        val timeInMillis = MutableLiveData<Long>()
        val timeInSeconds = MutableLiveData<Long>()
        val distanceInKm = MutableLiveData<Float>()
        val caloriesBurned = MutableLiveData<Int>()
        val avgSpeed = MutableLiveData<Float>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private var isFirstRun = true
    private var isServiceStopped = true

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let {
                    for (location in it) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        initValues()
        setObservers()

        fusedLocationProviderClient = FusedLocationProviderClient(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        Timber.d("Started tracking service")
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resumed tracking service")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused tracking service")
                    pauseForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped tracking service")
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setObservers() {
        timeInSeconds.observe(this, Observer {
            if (!isServiceStopped) {
                val builder = notificationBuilder.setContentText(
                    TimerUtil.getFormattedTime(it * 1000)
                )
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        })

        isTracking.observe(this, Observer {
            updateLocation(it)
        })
    }

    private fun pauseForegroundService() {
        isTracking.postValue(false)
        runEvent.postValue(RunEvent.PAUSE)
    }

    private var runTime = 0L
    private var lapTime = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyline()

        runEvent.postValue(RunEvent.START)
        isServiceStopped = false
        isTracking.postValue(true)

        val timeStarted = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeInMillis.postValue(lapTime + runTime)
                if (timeInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeInSeconds.postValue(timeInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(30L)
            }
            runTime += lapTime
        }
    }

    private fun initValues() {
        runEvent.postValue(RunEvent.END)
        timeInMillis.postValue(0L)
        timeInSeconds.postValue(0L)
        distanceInKm.postValue(0f)
        caloriesBurned.postValue(0)
        avgSpeed.postValue(0f)
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking: Boolean) {
        if (isTracking) {
            if (PermissionUtil.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(
                location.latitude,
                location.longitude
            )
            pathPoints.value?.apply {
                last().add(pos)

                updateDistance(last())
                updateAvgSpeed()
                updateCaloriesBurned()

                pathPoints.postValue(this)
            }

            Timber.d("New location: ${pos.latitude} ${pos.longitude}")
        }
    }

    private var distanceInKmTotal = 0f
    private var distanceInKmOfSinglePolyline = 0f
    private fun updateDistance(polyline: Polyline) {
        distanceInKmOfSinglePolyline = PolylineLengthUtil.calculatePolylineLength(polyline) / 1000f
        distanceInKmTotal += distanceInKmOfSinglePolyline
        distanceInKm.postValue(String.format(Locale.US,"%.2f",distanceInKmTotal).toFloat())
    }

    private var _avgSpeed = 0f
    private fun updateAvgSpeed() {
        _avgSpeed = round(distanceInKmTotal / ((lapTime + runTime) / 1000f / 60 / 60) * 10) / 10f
        avgSpeed.postValue(String.format(Locale.US,"%.2f",_avgSpeed).toFloat())
    }

    private var _caloriesBurned = 0
    private fun updateCaloriesBurned() {
        _caloriesBurned = (distanceInKmTotal * weight).toInt()
        caloriesBurned.postValue(_caloriesBurned)
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        isServiceStopped = false
        isTracking.postValue(true)
        addEmptyPolyline()

        runEvent.postValue(RunEvent.START)
        startTimer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        runEvent.postValue(RunEvent.END)
        isServiceStopped = true
        isFirstRun = true
        initValues()
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}