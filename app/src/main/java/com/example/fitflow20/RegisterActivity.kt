package com.example.fitflow20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loginText = findViewById<TextView>(R.id.login_text)
        val btnRegister = findViewById<Button>(R.id.btn_rgst)
        val emailRgstText = findViewById<EditText>(R.id.email_rgst)
        val passwordRgstText = findViewById<EditText>(R.id.password_rgst)

        val warn_lgn = findViewById<TextView>(R.id.warning_lgn)
        val warn_ps = findViewById<TextView>(R.id.warning_ps)

        //LOGIN INTENT HERE ================================================================================
        loginText.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // ======================================================================================================

        //if else cannot enter ==========================

        btnRegister.setOnClickListener() {
            var usisOKay: Boolean = false
            var psisOKay: Boolean = false

            if (TextUtils.isEmpty(emailRgstText.text.toString())) {
                emailRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                usisOKay = false
            } else {
                emailRgstText.setBackgroundResource(R.drawable.shape_edittext)
                usisOKay = true
            }

            //Add a function for taken username. --!!!!!!!!!!!!!--!!!!!!!!!!!!!!!--!!!!!!!!!!!!--!!!!!!!!!!!!!!!!!!--!!!!!!!!!!!!!!!

            if (TextUtils.isEmpty(passwordRgstText.text.toString())) {
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
            } else {
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext)
            }
            val Red = getResources().getColor(R.color.red)
            val Black = getResources().getColor(R.color.black)

            if (passwordRgstText.length() < 8) {
                warn_ps.setTextColor(Red)
                warn_ps.setText("Password cannot be less than 8 characters!!!!")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                psisOKay = false
            } else if (passwordRgstText.length() > 12) {
                warn_ps.setTextColor(Red)
                warn_ps.setText("Password cannot be more than 12 characters!!!!")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext_warn)
                psisOKay = false
            } else {
                warn_ps.setTextColor(Black)
                warn_ps.setText("Password is okay")
                passwordRgstText.setBackgroundResource(R.drawable.shape_edittext)
                psisOKay = true
            }

            if (psisOKay && usisOKay) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        //======================================================

    }
}