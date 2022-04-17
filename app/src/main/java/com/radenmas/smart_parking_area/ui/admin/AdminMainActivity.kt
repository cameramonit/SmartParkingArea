/*
 * Created by RadenMas on 18/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.admin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        b = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        sharedPref = getSharedPreferences(
            resources.getString(R.string.app_pref), Context.MODE_PRIVATE
        )!!
        editor = sharedPref.edit()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 5)
        }

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

        val checkin = Firebase.database.getReference("User").child(uidUser)
        val dataUser: MutableMap<String, Any> = HashMap()
        dataUser["checkin"] = timesNow
        checkin.updateChildren(dataUser).addOnSuccessListener {
            Handler(Looper.getMainLooper()).postDelayed({
                onResume()
            }, 5000)
        }
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
}