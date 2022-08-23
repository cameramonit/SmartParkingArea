/*
 * Created by RadenMas on 18/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.admin

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.zxing.Result
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.ActivityAdminMainBinding
import com.radenmas.smart_parking_area.ui.auth.AuthActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Created by RadenMas on 18/04/2022.
 */
class AdminMainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var b: ActivityAdminMainBinding
    private lateinit var scanner: ZXingScannerView

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var loginStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        b = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        sharedPref = getSharedPreferences(
            resources.getString(R.string.app_pref), Context.MODE_PRIVATE
        )!!
        editor = sharedPref.edit()

        loginStatus = sharedPref.getString(resources.getString(R.string.pref_level), "").toString()

        b.tvLevel.text = loginStatus

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 5)
        }

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val appName = resources.getString(R.string.app_name)

        checkUpdate(appName, versionName)

        scanner = ZXingScannerView(this)
        scanner.setAutoFocus(true)
        b.frameCamera.addView(scanner)

        b.imgLogout.setOnClickListener {
            editor.clear()
            editor.apply()

            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    override fun handleResult(p0: Result?) {
        val uidUser = p0?.text.toString()
        val timesNow = System.currentTimeMillis()

        val check = Firebase.database.getReference("User").child(uidUser)
        val dataUser: MutableMap<String, Any> = HashMap()
        dataUser[loginStatus] = timesNow

        check.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    check.updateChildren(dataUser).addOnSuccessListener {
                        onResume()
                    }

                    val status = Firebase.database.getReference(loginStatus)
                    status.setValue(1)
                } else {
                    Toast.makeText(
                        this@AdminMainActivity,
                        "User tidak terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                    onResume()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        scanner.setResultHandler(this)
        scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
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