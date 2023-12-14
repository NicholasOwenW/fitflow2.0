package com.example.fitflow20

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

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val loginText = findViewById<TextView>(R.id.login_text)
        val btnRgst = findViewById<Button>(R.id.btn_rgst)
        //LOGIN INTENT HERE ================================================================================
        loginText.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // ======================================================================================================

        //if else cannot enter ==========================
        val Red = getResources().getColor(R.color.red)
        val Black = getResources().getColor(R.color.black)

        binding.btnRgst.setOnClickListener() {


            val emailRgstText = findViewById<EditText>(R.id.email_rgst)
            val passwordRgstText = findViewById<EditText>(R.id.password_rgst)
            val userRgstText = findViewById<EditText>(R.id.username_rgst)

            val usernameData = binding.usernameRgst.text.toString()

            val warn_em = findViewById<TextView>(R.id.warning_em_rgst)
            val warn_us = findViewById<TextView>(R.id.warning_us_rgst)
            val warn_ps = findViewById<TextView>(R.id.warning_ps_rgst)

            var emisOkay: Boolean = false
            var emailInput = emailRgstText.text.toString()

            var usisOkay: Boolean = false
            var psisOKay: Boolean = false
            var passwordInput = passwordRgstText.text.toString()

            if (emailInput.isEmpty()) {
                emailRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                warn_em.setText("Well... email can't be empty silly :p")
                warn_em.setTextColor(Red)
            } else if (!emailInput.contains("@") || !emailInput.contains(".")) {
                emailRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                warn_em.setText("Email should be 'example@example.com'")
                warn_em.setTextColor(Red)
            } else {
                emailRgstText.setBackgroundResource(R.drawable.shape_edittext)
                warn_em.setText("Thats a cool email yo!")
                warn_em.setTextColor(Black)
                emisOkay = true
            }

            if (usernameData.isEmpty()) {
                userRgstText.setBackgroundResource((R.drawable.shape_edittext_warn))
                warn_us.setText("Don't be shy, pick a name")
                warn_us.setTextColor(Red)
                usisOkay = false
            } else if (usernameData.length > 20) {
                userRgstText.setBackgroundResource((R.drawable.shape_edittext_warn))
                warn_us.setText("That is waaaaaaaay too long, try another cool name under 20 characters :)")
                warn_us.setTextColor(Red)
            } else {
                userRgstText.setBackgroundResource(R.drawable.shape_edittext)
                warn_us.setText("That's a really cool name bro")
                warn_us.setTextColor(Black)
                usisOkay = true
            }

            //Add a function for taken username. --!!!!!!!!!!!!!--!!!!!!!!!!!!!!!--!!!!!!!!!!!!--!!!!!!!!!!!!!!!!!!--!!!!!!!!!!!!!!!

            if (passwordInput.isEmpty()) {
                warn_ps.setTextColor(Red)
                warn_ps.setText("Password is empty yo!")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
            } else if (passwordRgstText.length() < 8) {
                warn_ps.setTextColor(Red)
                warn_ps.setText("Password cannot be less than 8 characters!!!!")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                psisOKay = false
            } else if (passwordRgstText.length() > 16) {
                warn_ps.setTextColor(Red)
                warn_ps.setText("Password cannot be more than 16 characters!!!!")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                psisOKay = false
            } else {
                warn_ps.setTextColor(Black)
                warn_ps.setText("Password is okay dokey")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext)
                psisOKay = true
            }
            //======================================================

            val handler = Handler()
            handler.postDelayed(Runnable {
                if (psisOKay && usisOkay && emisOkay) {

                    database = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
                    val user = Users(usernameData)
                    database.child(usernameData).setValue(user)

                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)



                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
            }, 1500) // 1000 milliseconds = 1 second


        }
    }

}