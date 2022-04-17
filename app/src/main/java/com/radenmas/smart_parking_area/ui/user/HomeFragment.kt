/*
 * Created by RadenMas on 14/4/2022.
 * Copyright (c) 2022.
 */

package com.radenmas.smart_parking_area.ui.user

//import com.journeyapps.barcodescanner.BarcodeEncoder
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.radenmas.smart_parking_area.R
import com.radenmas.smart_parking_area.databinding.FragmentHomeBinding
import com.radenmas.smart_parking_area.ui.auth.AuthActivity
import com.radenmas.smart_parking_area.utils.Utils


/**
 * Created by RadenMas on 14/04/2022.
 */
class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding

    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val RESULT_OK = -1
    private var filePath: Uri? = null

    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var nipnim: String
    private lateinit var phone: String
    private lateinit var level: String
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
        editor = sharedPref.edit()

        initView()
        onClick()

        return v
    }

    private fun initView() {
        uid = sharedPref.getString(resources.getString(R.string.pref_uid), null).toString()
        name = sharedPref.getString(resources.getString(R.string.pref_name), null).toString()
        nipnim = sharedPref.getString(resources.getString(R.string.pref_nip_nim), null).toString()
        phone = sharedPref.getString(resources.getString(R.string.pref_phone), null).toString()
        level = sharedPref.getString(resources.getString(R.string.pref_level), null).toString()
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

        showQRCode()

        b.tvUserName.text = name
        b.tvName.text = name
        b.tvNimNip.text = nipnim
        b.tvStatus.text = level
        b.tvUserPhone.text = phone
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

                            editor.putString(
                                resources.getString(R.string.pref_avatar),
                                uri.toString()
                            )
                            editor.apply()

                            Glide.with(this)
                                .load(uri.toString())
                                .into(b.imgAvatar)
                            Glide.with(this)
                                .load(uri.toString())
                                .into(b.imgAvatarQR)
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