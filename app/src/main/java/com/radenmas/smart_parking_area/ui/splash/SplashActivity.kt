/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_splash)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val appName = resources.getString(R.string.app_name)

        checkUpdate(appName, versionName)
    }

    private fun checkUpdate(appName: String, versionName: String) {
        val defaultsRate = HashMap<String, Any>()
        defaultsRate["lates_app_version"] = versionName
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        config.setConfigSettingsAsync(configSettings)
        config.setDefaultsAsync(defaultsRate)
        config.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val newVersionCode = config.getString("lates_app_version")
                val lateAppVersion = newVersionCode.toFloat()
                if (lateAppVersion > versionName.toFloat()) {
                    showTheDialog(appName, newVersionCode)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    }, 1500)
                }
            }
        }
    }

    private fun showTheDialog(appName: String, versionName: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Update Aplikasi")
            .setMessage("Versi terbaru sudah tersedia! Update aplikasi $appName ke versi: $versionName")
            .setPositiveButton("Update", null)
            .show()

        dialog.setCancelable(false)

        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val uriUpdate =
                "https://wa.me/6285298106699?text=Tolong%20dikirimkan%20updatenya%20aplikasi%20Smart%20Parking%20Area%20versi%20$versionName"
            val uri = Uri.parse(uriUpdate)
            val update = Intent(Intent.ACTION_VIEW, uri)
            update.setPackage("com.whatsapp")
            try {
                startActivity(update)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(uriUpdate)
                    )
                )
            }
        }
    }
}