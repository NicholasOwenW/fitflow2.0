package com.example.fitflow20

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.math.log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val registerText = findViewById<TextView>(R.id.register_text)
        val emailLgnText = findViewById<EditText>(R.id.email_login)
        val passwordLgnText = findViewById<EditText>(R.id.password_login)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        val warn_em = findViewById<TextView>(R.id.warning_em_lgn)
        val warn_ps = findViewById<TextView>(R.id.warning_ps_lgn)

        val Red = getResources().getColor(R.color.red)
        val Black = getResources().getColor(R.color.black)

        // REGISTER INTENT HERE ===================================================================================
        registerText.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        //===========================================================================================================

        // if else cannot enter ===========================

        btnLogin.setOnClickListener() {

            var emailInput = emailLgnText.text.toString()
            var passwordInput = passwordLgnText.text.toString()

            var emisOkay: Boolean = false
            var psisOkay: Boolean = false

            if (emailInput.isEmpty()) {
                emailLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)
                warn_em.setText("Well... email can't be empty silly :p")
                warn_em.setTextColor(Red)
            } else if (!emailInput.contains("@") || !emailInput.contains(".")) {
                emailLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)
                warn_em.setText("Email should be 'example@example.com'")
                warn_em.setTextColor(Red)
            } else {
                emailLgnText.setBackgroundResource(R.drawable.shape_edittext)
                warn_em.setText("Okay")
                warn_em.setTextColor(Black)
                emisOkay = true
            }

            if (passwordInput.isEmpty()) {
                passwordLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)
                warn_ps.setTextColor(Red)
                warn_ps.setText("Well, this is awkward (Password is empty)")
            } else {
                passwordLgnText.setBackgroundResource(R.drawable.shape_edittext)
                warn_ps.setTextColor(Black)
                warn_ps.setText("")
                psisOkay = true

                if (emisOkay && psisOkay) {
                    auth.signInWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                emailLgnText.setBackgroundResource(R.drawable.shape_edittext)
                                warn_em.setTextColor(Black)
                                warn_em.setText("Okay")
                                passwordLgnText.setBackgroundResource(R.drawable.shape_edittext)
                                warn_ps.setTextColor(Black)
                                warn_ps.setText("Okay")
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                emailLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)
                                warn_em.setTextColor(Red)
                                warn_em.setText("Are you sure this is your email?")

                                passwordLgnText.setBackgroundResource(R.drawable.shape_edittext_warn)
                                warn_ps.setTextColor(Red)
                                warn_ps.setText("Are you sure the password is right?")
                            }
                        }
                }
            }
            //======================================================

        }
    }
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
