package com.example.triathlon360

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEdit = findViewById<EditText>(R.id.edit_name)
        val mobileEdit = findViewById<EditText>(R.id.edit_mobile_register)
        val passwordEdit = findViewById<EditText>(R.id.edit_password)
        val registerButton = findViewById<Button>(R.id.button_register)
        val loginLink = findViewById<TextView>(R.id.text_login_link)

        registerButton.setOnClickListener {
            val name = nameEdit.text.toString().trim()
            val mobile = mobileEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (name.isBlank() || mobile.length != 10 || password.length < 4) {
                Toast.makeText(
                    this,
                    "enter name, 10 digit mobile and min 4 char password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // for now, just show a message and go back to login
                // later we can connect this with Firebase Auth / Firestore users
                Toast.makeText(this, "account created for $name", Toast.LENGTH_SHORT).show()
                finish() // goes back to login screen
            }
        }

        loginLink.setOnClickListener {
            // go back to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
