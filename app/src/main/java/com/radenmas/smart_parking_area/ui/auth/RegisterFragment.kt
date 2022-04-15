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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.radenmas.smart_parking_area.databinding.FragmentRegisterBinding
import com.radenmas.smart_parking_area.utils.Utils

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
        b.btnRegister.setOnClickListener {
            val strFullName: String = b.etFullName.text.toString()
            val strEmail: String = b.etEmail.text.toString()
            val strPhone: String = b.etEmail.text.toString()
            val strPassword: String = b.etPassword.text.toString()

            if (strFullName.isEmpty() || strEmail.isEmpty() || strPhone.isEmpty() || strPassword.isEmpty()) {
                Utils.toast(requireContext(), "Lengkapi yang masih kosong")
            } else {
                Utils.showLoading(requireContext())

                register(strFullName, strEmail, strPhone, strPassword)
            }
        }

        b.tvLogin.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun register(
        strFullName: String,
        strEmail: String,
        strPhone: String,
        strPassword: String
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(strEmail, strPassword)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid.toString()

                val dbUser =
                    FirebaseDatabase.getInstance().getReference("User").child(uid)
                val dataUser: MutableMap<String, Any> = HashMap()
                dataUser["uid"] = uid
                dataUser["name"] = strFullName
                dataUser["email"] = strEmail
                dataUser["password"] = strPassword
                dataUser["phone"] = strPhone
                dataUser["level"] = "user"
                dataUser["avatar"] = "default"
                dbUser.setValue(dataUser)

                Utils.dismissLoading()
                Utils.toast(requireContext(), "Berhasil mendaftarkan akun")

                b.etFullName.text.clear()
                b.etEmail.text.clear()
                b.etPhone.text.clear()
                b.etPassword.text.clear()
            }.addOnFailureListener {
                Utils.dismissLoading()
                Utils.toast(requireContext(), it.message.toString())
            }
    }
}