/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.radenmas.smart_parking_area.databinding.FragmentProfileBinding

/**
 * Created by RadenMas on 14/04/2022.
 */
class ProfileFragment : Fragment() {
    private lateinit var b: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentProfileBinding.inflate(layoutInflater, container, false)
        val v = b.root

        initView()
        onClick()

        return v
    }

    private fun initView() {

    }

    private fun onClick() {
        b.imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}