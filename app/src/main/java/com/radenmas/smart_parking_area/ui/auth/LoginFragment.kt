/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.FragmentLoginBinding
import com.radenmas.smart_parking_area.ui.admin.AdminMainActivity
import com.radenmas.smart_parking_area.ui.user.UserMainActivity
import com.radenmas.smart_parking_area.utils.Utils

/**
 * Created by RadenMas on 14/04/2022.
 */
class LoginFragment : Fragment() {

    private lateinit var b: FragmentLoginBinding

    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val v = b.root

        sharedPref = activity?.getSharedPreferences(
            resources.getString(R.string.app_pref), Context.MODE_PRIVATE
        )!!
        editor = sharedPref.edit()

        val loginStatus: String? =
            sharedPref.getString(resources.getString(R.string.pref_level), "")

        when (loginStatus.toString()) {
            resources.getString(R.string.dosen),
            resources.getString(R.string.mahasiswa) -> {
                startActivity(Intent(context, UserMainActivity::class.java))
                activity?.finish()
            }

            resources.getString(R.string.pref_checkout),
            resources.getString(R.string.pref_checkin) -> {
                startActivity(Intent(context, AdminMainActivity::class.java))
                activity?.finish()
            }
        }
        onClick()

        return v
    }

    private fun onClick() {
        b.btnLogin.setOnClickListener {
            val strEmail: String = b.etEmail.text.toString()
            val strPassword: String = b.etPassword.text.toString()

            if (strEmail.isEmpty() || strPassword.isEmpty()) {
                Toast.makeText(context, "Lengkapi yang masih kosong", Toast.LENGTH_SHORT).show()
            } else {
                Utils.showLoading(requireContext())

                login(strEmail, strPassword)
            }
        }

        b.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun login(strEmail: String, strPassword: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(strEmail, strPassword)
            .addOnSuccessListener {
                val uid = it.user?.uid.toString()

                FirebaseDatabase.getInstance().getReference("User").child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            when (val level = snapshot.child("level").value.toString()) {
                                resources.getString(R.string.dosen),
                                resources.getString(R.string.mahasiswa) -> {
                                    editor.putString(
                                        resources.getString(R.string.pref_level),
                                        level
                                    )
                                    editor.apply()

                                    startActivity(Intent(context, UserMainActivity::class.java))
                                    activity?.finish()
                                }

                                resources.getString(R.string.pref_checkout),
                                resources.getString(R.string.pref_checkin) -> {
                                    editor.putString(
                                        resources.getString(R.string.pref_level),
                                        level
                                    )
                                    editor.apply()

                                    startActivity(Intent(context, AdminMainActivity::class.java))
                                    activity?.finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                Utils.dismissLoading()

            }.addOnFailureListener {
                Utils.dismissLoading()
                Utils.toast(requireContext(), it.message.toString())
            }
    }
}