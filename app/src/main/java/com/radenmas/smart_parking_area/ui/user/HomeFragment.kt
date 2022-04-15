/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.user

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.FragmentHomeBinding
import com.radenmas.smart_parking_area.utils.Utils

/**
 * Created by RadenMas on 14/04/2022.
 */
class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding

    private lateinit var sharedPref: SharedPreferences

    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var avatar: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val v = b.root

        sharedPref = activity?.getSharedPreferences(
            resources.getString(R.string.app_pref), Context.MODE_PRIVATE
        )!!

        initView()
        onClick()

        return v
    }

    private fun initView() {
        uid = sharedPref.getString(resources.getString(R.string.pref_uid), null).toString()
        name = sharedPref.getString(resources.getString(R.string.pref_name), null).toString()
        avatar = sharedPref.getString(resources.getString(R.string.pref_avatar), null).toString()

        if (avatar == resources.getString(R.string.def)) {
            Glide.with(this)
                .load(R.drawable.ic_profile_default)
                .into(b.imgAvatar)
            Glide.with(this)
                .load(R.drawable.ic_profile_default)
                .into(b.imgAvatarQR)
        } else {
            Glide.with(this)
                .load(avatar)
                .into(b.imgAvatar)
            Glide.with(this)
                .load(avatar)
                .into(b.imgAvatarQR)
        }

        val writer = MultiFormatWriter()
        try {
            val matrix: BitMatrix = writer.encode(uid, BarcodeFormat.QR_CODE, 300, 300)
            val encoder = BarcodeEncoder()
            val bitmap: Bitmap = encoder.createBitmap(matrix)
            Glide.with(this)
                .load(bitmap)
                .into(b.imgQRCode)
        } catch (e: Exception) {
            Utils.dismissLoading()
            Utils.toast(requireContext(), e.message.toString())
        }

        b.tvUserName.text = name
        b.tvUserID.text = uid
    }

    private fun onClick() {
        b.imgAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }
}