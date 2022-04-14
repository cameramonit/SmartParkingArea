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
import androidx.navigation.fragment.findNavController
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.FragmentLoginBinding

/**
 * Created by RadenMas on 14/04/2022.
 */
class LoginFragment : Fragment() {
    private lateinit var b: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val v = b.root

        initView()
        onClick()

        return v
    }

    private fun initView() {

    }

    private fun onClick() {
        b.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}