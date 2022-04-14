/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.radenmas.smart_parking_area.databinding.FragmentRegisterBinding

/**
 * Created by RadenMas on 14/04/2022.
 */
class RegisterFragment : Fragment() {
    private lateinit var b: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        val v = b.root

        initView()
        onClick()

        return v
    }

    private fun initView() {

    }

    private fun onClick() {
        b.tvLogin.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}