package com.example.fitflow20

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitflow20.databinding.ActivityMainBinding
import com.example.fitflow20.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

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

            registrasiUser(email, password, userNama, phoneNumber)
        }
    }

    private fun registrasiUser(email: String, password: String, userNama: String, phoneNumber: String){
        val progressDialog = ProgressDialog(this@RegisterActivity)
        progressDialog.setTitle("Registratation User")
        progressDialog.setMessage("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
            if(it.isSuccessful){
                saveUser(userNama, email, phoneNumber, progressDialog)
            }else{
                val message = it.exception!!. toString()
                Toast.makeText(this, "Error : $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(userNama: String, email: String, phoneNumber: String, progressDialog: ProgressDialog) {
        val currentUserId = auth.currentUser!!.uid
        ref = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("USERS")
        val userMap = HashMap<String, Any>()
        userMap["id"]=currentUserId
        userMap["userNama"]= userNama
        userMap["email"]=email
        userMap["phoneNumber"]=phoneNumber

        ref.child(currentUserId).setValue(userMap).addOnCompleteListener {
            if(it.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this, "Register is Successfuly", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, HomeActivity :: class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                val message = it.exception!!. toString()
                Toast.makeText(this, "Error : $message", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }

}