package com.example.fitflow20

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fitflow20.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var binding : ActivityRegisterBinding
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("USERS")

        binding.btnRegister.setOnClickListener{
            val userNama = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phoneNumber = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val workoutDays = mapOf(
                "Monday" to listOf(""),
                "Tuesday" to listOf(""),
                "Wednesday" to listOf(""),
                "Thursday" to listOf(""),
                "Friday" to listOf(""),
                "Saturday" to listOf(""),
                "Sunday" to listOf(""),
            )

            if(email.isEmpty()){
                binding.etEmail.error = "Email required"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty() || password.length < 8){
                binding.etPassword.error = "Password required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            if(userNama.isEmpty()){
                binding.etName.error = "Full Name required"
                binding.etName.requestFocus()
                return@setOnClickListener
            }
            if(phoneNumber.isEmpty()){
                binding.etPhone.error = "Phone Number required"
                binding.etPhone.requestFocus()
                return@setOnClickListener
            }

            registrasiUser(email, password, userNama, phoneNumber, workoutDays)
        }
    }

    private fun registrasiUser(
        email: String,
        password: String,
        userNama: String,
        phoneNumber: String,
        workoutDays: Map<String, List<String>>
    ){
        val progressDialog = ProgressDialog(this@RegisterActivity)
        progressDialog.setTitle("Registration User")
        progressDialog.setMessage("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
            if(it.isSuccessful){
                saveUser(email, phoneNumber, userNama, workoutDays, progressDialog)
            } else {
                val message = it.exception!!.toString()
                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(
        email: String,
        phoneNumber: String,
        userNama: String,
        workoutDays: Map<String, List<String>>,
        progressDialog: ProgressDialog
    ) {
        val currentUserId = auth.currentUser!!.uid
        ref = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("USERS")
        val userMap = HashMap<String, Any>()
        userMap["id"] = currentUserId
        userMap["userNama"] = userNama
        userMap["email"] = email
        userMap["phoneNumber"] = phoneNumber
        userMap["workoutDays"] = workoutDays

        val resourceId = resources.getIdentifier("pfp", "drawable", packageName)
        val drawable = ContextCompat.getDrawable(this, resourceId)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Debugging: Output the currentUserId
        println("Current User ID: $currentUserId")

        val imgref = FirebaseStorage.getInstance("gs://fitflow-id.appspot.com")
            .reference
            .child("img/$currentUserId.jpg")

        imgref.putBytes(data)
            .addOnSuccessListener {
                // Debugging: Log success message
            }
            .addOnFailureListener { e ->
                // Debugging: Log failure message and exception
                println("Image Upload Failed: ${e.message}")
            }

        ref.child(currentUserId).setValue(userMap).addOnCompleteListener {
            if(it.isSuccessful){
                progressDialog.dismiss()
//                Toast.makeText(this, "Register is Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, HomeActivity :: class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val message = it.exception!!.toString()
                // Debugging: Log error message
                println("Error: $message")
                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }
}
