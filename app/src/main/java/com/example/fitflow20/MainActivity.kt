package com.example.fitflow20

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerText = findViewById<TextView>(R.id.register_text)
        val emailLgnText = findViewById<EditText>(R.id.email_login)
        val passwordLgnText = findViewById<EditText>(R.id.password_login)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        //REGISTER INTENT HERE ===================================================================================
        registerText.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        //===========================================================================================================

        //if else cannot enter ===========================
        btnLogin.setOnClickListener() {
            if (TextUtils.isEmpty(emailLgnText.text.toString())) {
                Log.d("EMPTY CHAR", "EMPTY EMPTY EMPTY EMPTY EMPTY")
                emailLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)

            } else {
                Log.d("Char is ", emailLgnText.text.toString())
                emailLgnText.setBackgroundResource(R.drawable.shape_edittext)
            }

            if (TextUtils.isEmpty(passwordLgnText.text.toString())) {
                passwordLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)
            } else {
                passwordLgnText.setBackgroundResource(R.drawable.shape_edittext)
            }
        }
        //======================================================
    }
}