package com.example.fitnessaplikacia.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.models.Run
import com.example.fitnessaplikacia.models.RunEvent
import com.example.fitnessaplikacia.services.Polyline
import com.example.fitnessaplikacia.services.TrackingService
import com.example.fitnessaplikacia.utility.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.fitnessaplikacia.utility.Constants.ACTION_STOP_SERVICE
import com.example.fitnessaplikacia.utility.Constants.MAP_ZOOM
import com.example.fitnessaplikacia.utility.Constants.POLYLINE_COLOR
import com.example.fitnessaplikacia.utility.Constants.POLYLINE_WIDTH
import com.example.fitnessaplikacia.utility.TimerUtil
import com.example.fitnessaplikacia.viewModels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private val viewModel: MainViewModel by viewModels()

    lateinit var googleMap: GoogleMap
    private var toggle = true

    private var pathPoints = mutableListOf<Polyline>()

    private var distance = 0f
    private var caloriesBurned = 0
    private var curTimeInMillis = 0L
    private var avgSpeed = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleMapView.onCreate(savedInstanceState)
        googleMapView.getMapAsync {
            googleMap = it
            addAllPolylines()

            setObservers()
            setToggleListener()
        }

        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(
            R.id.action_runFragment_to_runsFragment
        )
    }

    private fun saveRunToDB() {
        googleMap.snapshot {
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val run = Run(it, dateTimeStamp, curTimeInMillis, distance, avgSpeed, caloriesBurned)
            CoroutineScope(Dispatchers.Main)

            viewModel.insertRun(run)
        }

        stopRun()
    }

    private fun showCancelRunDialog() {
        val dialog = MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle("Zrušiť beh?")
            .setMessage("Chcete ukončiť beh?")
            .setIcon(R.drawable.stop_circle)
            .setPositiveButton("Áno") {_, _ ->
                saveRunToDB()
            }
            .setNegativeButton("Nie") {dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
        for(polyLine in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyLine)
            googleMap.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last()[pathPoints.last().size - 1]

            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            googleMap.addPolyline(polylineOptions)
        }
    }

    private fun setObservers() {
        TrackingService.runEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                RunEvent.START -> {
                    fabPauseRun.setImageResource(R.drawable.pause)
                }
                RunEvent.PAUSE -> {
                    fabPauseRun.setImageResource(R.drawable.play)
                }
            }
        })

        TrackingService.timeInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            tvTimeDescription.text = TimerUtil.getFormattedTime(curTimeInMillis,true)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.distanceInKm.observe(viewLifecycleOwner, Observer {
            distance = it
            tvDistanceDescription.text = "$distance km"
        })

        TrackingService.avgSpeed.observe(viewLifecycleOwner, Observer {
            avgSpeed = it
            tvAvgSpeedDescription.text = "$avgSpeed km/h"
        })

        TrackingService.caloriesBurned.observe(viewLifecycleOwner, Observer {
            caloriesBurned = it
            tvCaloriesBurned.text = "$caloriesBurned kcal"
        })
    }

    private fun setToggleListener() {
        fabPauseRun.setOnClickListener {
            if (toggle) {
                sendCommandToService(ACTION_PAUSE_SERVICE)
                toggle = false
            } else {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                toggle = true
            }
        }
        fabStopRun.setOnClickListener {
            showCancelRunDialog()
        }
    }

    private fun sendCommandToService(action: String) {
        requireContext().startService(
            Intent(requireContext(), TrackingService::class.java).apply {
                this.action = action
            }
        )
    }

    override fun onResume() {
        super.onResume()
        googleMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        googleMapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        googleMapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        googleMapView.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        googleMapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        googleMapView.onSaveInstanceState(outState)
    }
}