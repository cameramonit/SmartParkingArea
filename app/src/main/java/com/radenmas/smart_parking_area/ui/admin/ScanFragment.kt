/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
//import com.google.zxing.integration.android.IntentIntegrator
import com.radenmas.smart_parking_area.databinding.FragmentScanBinding

/**
 * Created by RadenMas on 14/04/2022.
 */
class ScanFragment : Fragment() {
    private lateinit var b: FragmentScanBinding

//    private lateinit var qrScanIntegrator: IntentIntegrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentScanBinding.inflate(layoutInflater, container, false)
        val v = b.root

        initView()
        onClick()

//        qrScanIntegrator = IntentIntegrator.forSupportFragment(this)
//        qrScanIntegrator.initiateScan()
//        qrScanIntegrator.setPrompt("Arahkan kamera ke QR Code")
//        qrScanIntegrator.setBeepEnabled(true)
//        qrScanIntegrator.setCameraId(0)
//        qrScanIntegrator.setBeepEnabled(false)
//        qrScanIntegrator.setBarcodeImageEnabled(true)
//        qrScanIntegrator.setOrientationLocked(false)
//        qrScanIntegrator.captureActivity = Capture::class.java

        return v
    }

    private fun initView() {

    }

    private fun onClick() {
    }
}