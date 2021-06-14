package com.example.fitnessaplikacia.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.utility.Constants.KEY_NAME
import com.example.fitnessaplikacia.utility.Constants.REQUEST_CODE_LOCATION
import com.example.fitnessaplikacia.utility.PermissionUtil
import com.example.fitnessaplikacia.viewModels.StatisticsViewModel
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

    private val viewModel: StatisticsViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGreeting()
        setObservers()

        fabStart.setOnClickListener {
            if (!PermissionUtil.hasLocationPermissions(requireContext())) {
                requestLocationPermissions()
            } else {
                navigateToRunFragmentIfLocationEnabled()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setGreeting() {
        val name = sharedPref.getString(KEY_NAME, "")
        tvGreeting.text = "Ahoj ${name} :)"
    }

    private fun navigateToRunFragmentIfLocationEnabled() {
        if(!isLocationEnabled(requireContext())) {
            AlertDialog.Builder(requireContext())
                .setTitle("Poloha")
                .setMessage("Potrebujete zapnúť polohu")
                .setPositiveButton("Nastavenia", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
                .setNegativeButton("Zrušiť", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })
                .show()
        } else {
            findNavController().navigate(
                R.id.action_startRunFragment_to_runFragment
            )
        }
    }

    private fun setObservers() {
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            tvTotalDistance.text = if (it != null) "${String.format("%.2f",it)} km" else "0 km"
        })
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

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        navigateToRunFragmentIfLocationEnabled()
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