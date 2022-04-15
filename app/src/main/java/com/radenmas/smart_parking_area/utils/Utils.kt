/*
 * Created by RadenMas on 21/3/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.radenmas.smart_parking_area.R

/**
 * Created by RadenMas on 21/03/2022.
 */
object Utils {
    private lateinit var progress: Dialog

    fun showLoading(context: Context) {
        progress = Dialog(context)
        progress.setContentView(R.layout.dialog_progress)
        progress.window!!.setBackgroundDrawableResource(R.drawable.bg_progress)
        progress.show()
    }

    fun dismissLoading() {
        progress.dismiss()
    }

    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}