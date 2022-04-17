/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.auth

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.FragmentRegisterBinding
import com.radenmas.smart_parking_area.utils.Utils

/**
 * Created by RadenMas on 14/04/2022.
 */
class RegisterFragment : Fragment() {
    private lateinit var b: FragmentRegisterBinding

    var status: String? = null

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onClick() {
        b.rbDosen.setOnClickListener {
            status = resources.getString(R.string.dosen)
            b.rbDosen.setTextColor(ResourcesCompat.getColor(resources, R.color.primary_text, null))
            b.rbDosen.background = resources.getDrawable(R.drawable.bg_edit_text_selected,null)
            b.rbMahasiswa.setTextColor(ResourcesCompat.getColor(resources, R.color.hint, null))
            b.rbMahasiswa.background = resources.getDrawable(R.drawable.bg_edit_text_normal,null)
        }

        b.rbMahasiswa.setOnClickListener {
            status = resources.getString(R.string.mahasiswa)
            b.rbMahasiswa.setTextColor(ResourcesCompat.getColor(resources, R.color.primary_text, null))
            b.rbMahasiswa.background = resources.getDrawable(R.drawable.bg_edit_text_selected,null)
            b.rbDosen.setTextColor(ResourcesCompat.getColor(resources, R.color.hint, null))
            b.rbDosen.background = resources.getDrawable(R.drawable.bg_edit_text_normal,null)
        }

        b.btnRegister.setOnClickListener {
            val strFullName: String = b.etFullName.text.toString()
            val strNipNim: String = b.etNipNim.text.toString()
            val strEmail: String = b.etEmail.text.toString()
            val strPhone: String = b.etPhone.text.toString()
            val strPassword: String = b.etPassword.text.toString()
            val strRepeatPassword: String = b.etRepeatPassword.text.toString()

            if (strFullName.isEmpty() || strNipNim.isEmpty() || strEmail.isEmpty()
                || strPhone.isEmpty() || strPassword.isEmpty() || status.isNullOrEmpty()) {
                Utils.toast(requireContext(), "Lengkapi yang masih kosong")
            } else if (strPassword != strRepeatPassword) {
                Utils.toast(requireContext(), "Password tidak sama")
            } else {
                Utils.showLoading(requireContext())

                register(strFullName, strNipNim, strEmail, strPhone, strPassword, status!!)
            }
        }

        b.tvLogin.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun register(
        strFullName: String,
        strNipNim: String,
        strEmail: String,
        strPhone: String,
        strPassword: String,
        strStatus: String
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(strEmail, strPassword)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid.toString()

                val dbUser =
                    FirebaseDatabase.getInstance().getReference("User").child(uid)
                val dataUser: MutableMap<String, Any> = HashMap()
                dataUser["uid"] = uid
                dataUser["name"] = strFullName
                dataUser["nip_nim"] = strNipNim
                dataUser["email"] = strEmail
                dataUser["password"] = strPassword
                dataUser["phone"] = strPhone
                dataUser["level"] = strStatus
                dataUser["checkin"] = "-"
                dataUser["avatar"] = "default"
                dbUser.setValue(dataUser)

                Utils.dismissLoading()
                Utils.toast(requireContext(), "Berhasil mendaftarkan akun")

                b.etFullName.text.clear()
                b.etNipNim.text.clear()
                b.etEmail.text.clear()
                b.etPhone.text.clear()
                b.etPassword.text.clear()
                b.etRepeatPassword.text.clear()
            }.addOnFailureListener {
                Utils.dismissLoading()
                Utils.toast(requireContext(), it.message.toString())
            }
    }
}