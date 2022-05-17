/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.FragmentHomeBinding
import com.radenmas.smart_parking_area.ui.auth.AuthActivity
import com.radenmas.smart_parking_area.utils.Utils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by RadenMas on 14/04/2022.
 */
class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val RESULT_OK = -1
    private var filePath: Uri? = null

    private lateinit var dtUser: DatabaseReference

    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val v = b.root

        sharedPref = activity?.getSharedPreferences(
            resources.getString(R.string.app_pref), Context.MODE_PRIVATE
        )!!
        editor = sharedPref.edit()

        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        dtUser = FirebaseDatabase.getInstance().getReference("User").child(uid)

        initView()
        getData()
        onClick()

        return v
    }

    private fun getData() {
        dtUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val nip_nim = snapshot.child("nip_nim").value.toString()
                    val level = snapshot.child("level").value.toString()
                    val phone = snapshot.child("phone").value.toString()
                    val avatar = snapshot.child("avatar").value.toString()
                    val checkIn = snapshot.child("checkin").value.toString()
                    val checkOut = snapshot.child("checkout").value.toString()

                    b.tvUserName.text = name
                    b.tvName.text = name
                    b.tvNimNip.text = nip_nim
                    b.tvStatus.text = level
                    b.tvUserPhone.text = phone
                    if (checkIn != "-") {
                        b.tvCheckIn.text = convertLongToTime(checkIn.toLong())
                    } else {
                        b.tvCheckIn.text = "-"
                    }

                    if (checkOut != "-") {
                        b.tvCheckOut.text = convertLongToTime(checkOut.toLong())
                    } else {
                        b.tvCheckOut.text = "-"
                    }

                    if (avatar == resources.getString(R.string.def)) {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_profile_default)
                            .into(b.imgAvatar)
                        Glide.with(requireContext())
                            .load(R.drawable.ic_profile_default)
                            .into(b.imgAvatarQR)
                    } else {
                        Glide.with(requireContext())
                            .load(avatar)
                            .into(b.imgAvatar)
                        Glide.with(requireContext())
                            .load(avatar)
                            .into(b.imgAvatarQR)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd MMM yyyy HH:mm")
        return format.format(date)
    }


    private fun initView() {
        showQRCode()
    }

    private fun showQRCode() {
        try {
            val matrix = MultiFormatWriter().encode(
                uid,
                BarcodeFormat.QR_CODE,
                300, 300
            )

            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (matrix[x, y]) BLACK else WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            Glide.with(this)
                .load(bitmap)
                .into(b.imgQRCode)
        } catch (er: Exception) {
        }
    }

    private fun onClick() {
        b.imgAvatar.setOnClickListener {
            chooseFoto()
        }
        b.imgChangeProfile.setOnClickListener {
            chooseFoto()
        }

        b.imgLogout.setOnClickListener {
            editor.clear()
            editor.apply()

            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(context, AuthActivity::class.java))
            activity?.finish()
        }
    }

    private fun chooseFoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 71)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 71 && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            uploadImage()
        }
    }

    private fun uploadImage() {
        val storageReference =
            FirebaseStorage.getInstance().getReference("User").child(uid)
        if (filePath != null) {
            Utils.showLoading(requireContext())

            val ref = storageReference.child(uid)
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener { uri ->
                            val dbUser =
                                FirebaseDatabase.getInstance().getReference("User").child(uid)
                            val dataUser: MutableMap<String, Any> = HashMap()
                            dataUser["avatar"] = uri.toString()
                            dbUser.updateChildren(dataUser)

                            Utils.dismissLoading()
                            Utils.toast(
                                requireContext(), "Foto profil berhasil diubah"
                            )
                        }
                }
                .addOnFailureListener {
                    Utils.dismissLoading()
                    Utils.toast(requireContext(), "Gagal mengubah foto profil")
                }
        } else {
            Utils.toast(requireContext(), "Gambar belum dipilih")
        }
    }
}