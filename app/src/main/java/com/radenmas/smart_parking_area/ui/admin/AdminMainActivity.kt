/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.admin

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.radenmas.smart_parking_area.databinding.ActivityAdminMainBinding
import com.radenmas.smart_parking_area.utils.Utils
import me.dm7.barcodescanner.zxing.ZXingScannerView

class AdminMainActivity : AppCompatActivity() {
    private lateinit var b: ActivityAdminMainBinding
    private lateinit var scanner: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        b = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        scanner = ZXingScannerView(this)
        scanner.setAutoFocus(true)
        scanner.setResultHandler {
            Utils.toast(this, it.text.toString())
        }
        b.frameCamera.addView(scanner)
    }

//    override fun handleResult(result: Result?) {
//        Utils.toast(this, result?.text.toString())
//    }
}
