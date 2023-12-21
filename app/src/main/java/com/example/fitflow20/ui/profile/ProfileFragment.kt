package com.example.fitflow20.ui.profile

import Users
import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fitflow20.MainActivity
import com.example.fitflow20.R
import com.example.fitflow20.RegisterActivity
import com.example.fitflow20.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private var updatephone = "0"
    private lateinit var imgUser: CircleImageView
    private lateinit var imageUri: Uri
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_CAMERA = 100
        const val CAMERA_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val tv_user = view.findViewById<TextView>(R.id.tv_user)
        val tv_isi_email = view.findViewById<TextView>(R.id.t_isi_email)
        val tv_isi_phone = view.findViewById<TextView>(R.id.t_isi_phone)

        // Load user information
        userInfo()

        val editBtn = view.findViewById<TextView>(R.id.edit_phone)

        editBtn.setOnClickListener() {
            showCustomDialogBoxConfirm()
        }

        val logoutBtn = view.findViewById<Button>(R.id.btn_logout)

        logoutBtn.setOnClickListener() {
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        imgUser = view.findViewById(R.id.imgUser)

        imgUser.setOnClickListener {
            checkCameraPermission()
        }

        // Load user profile image
        loadProfileImage()

        return view
    }

    private fun loadProfileImage() {
        val imageRef = FirebaseStorage.getInstance("gs://fitflow-id.appspot.com")
            .reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        imageRef.downloadUrl.addOnCompleteListener {
            it.result?.let { uri ->
                // Load the image into imgUser using your preferred image loading library
                // (e.g., Glide, Picasso)
                // For example using Glide:
                Glide.with(this)
                    .load(uri)
                    .into(imgUser)
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            intentCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }


    private fun showCustomDialogBoxConfirm() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_edit_phone)

        val newphone: EditText = dialog.findViewById(R.id.new_phone)
        val btnEdit: Button = dialog.findViewById(R.id.confirm_edit)
        val btnCancel: Button = dialog.findViewById(R.id.cancel_edit)

        btnEdit.setOnClickListener() {
            val updatedPhone = newphone.text.toString().trim()
            if (updatedPhone.isNotEmpty()) {
                updatephone = updatedPhone
                Log.d("TAGGERSSSSSSSSSSSSSSSS", "Here is updatephone: $updatephone")
                updatePhoneNumber(updatedPhone)
                dialog.dismiss()
            } else {
                // Handle empty phone number input, show a message or take appropriate action
            }
        }

        btnCancel.setOnClickListener() {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("USERS").child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<Users>(Users::class.java)
                    user?.let {
                        val tv_user = view?.findViewById<TextView>(R.id.tv_user)
                        val tv_isi_email = view?.findViewById<TextView>(R.id.t_isi_email)
                        val tv_isi_phone = view?.findViewById<TextView>(R.id.t_isi_phone)

                        tv_user?.text = it.userNama
                        tv_isi_email?.text = it.email
                        tv_isi_phone?.text = it.phoneNumber

                        // Check if the 'workoutDays' field exists in the snapshot
                        if (snapshot.hasChild("workoutDays")) {
                            // Assuming you want to join the workouts for 'Monday' into a string
                            val mondayWorkouts = it.workoutDays["Monday"]?.joinToString(", ") ?: "No workouts for Monday"
                            Log.d("TAG", "Monday Workouts: $mondayWorkouts")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
                Log.e("TAG", "Error reading data from Firebase: ${error.message}")
            }
        })
    }


    private fun updatePhoneNumber(newPhone: String) {
        val userRef =
            FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference.child("USERS").child(firebaseUser.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userRef.child("phoneNumber").setValue(newPhone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgUser.setOnClickListener {
            intentCamera()
        }
    }

    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {

        val baos = ByteArrayOutputStream()
        val ref =
            FirebaseStorage.getInstance("gs://fitflow-id.appspot.com").reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener {
                        it.result?.let {
                            imageUri = it
                            imgUser.setImageBitmap(imgBitmap)
                        }
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied. Unable to take a photo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
