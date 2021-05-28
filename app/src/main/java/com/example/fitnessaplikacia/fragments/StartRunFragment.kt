package com.example.fitnessaplikacia.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.KEY_NAME
import com.example.fitnessaplikacia.utility.Constants.REQUEST_CODE_LOCATION
import com.example.fitnessaplikacia.utility.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_start_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class StartRunFragment : Fragment(R.layout.fragment_start_run),
    EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGreeting()

        fabStart.setOnClickListener {
            if (!PermissionUtil.hasLocationPermissions(requireContext())) {
                requestLocationPermissions()
            } else {
                findNavController().navigate(
                    R.id.action_startRunFragment_to_runFragment
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setGreeting() {
        val name = sharedPref.getString(KEY_NAME, "")
        tvGreeting.text = "Ahoj ${name} :)"
    }

    private fun requestLocationPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "Potrebujete povoliť polohu pre začatie behu",
                REQUEST_CODE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Potrebujete povoliť polohu pre túto aplikáciu",
                REQUEST_CODE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        findNavController().navigate(
            R.id.action_startRunFragment_to_runFragment
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setTitle("Potrebujete povolenie")
                .setRationale("Táto aplikácia potrebuje povolenie snímania polohy. Zmeňte povolenie v nastaveniach.")
                .setPositiveButton("Nastavenia")
                .build().show()
        } else {
            requestLocationPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}