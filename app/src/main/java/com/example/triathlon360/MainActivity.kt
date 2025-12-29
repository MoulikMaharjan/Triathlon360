package com.example.triathlon360

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mobileEdit = findViewById<EditText>(R.id.edit_mobile)
        val loginButton = findViewById<Button>(R.id.button_login)
        val registerLink = findViewById<TextView>(R.id.text_register_link)

        loginButton.setOnClickListener {
            val mobile = mobileEdit.text.toString().trim()

            if (mobile.length != 10) {
                Toast.makeText(this, "enter 10 digit mobile", Toast.LENGTH_SHORT).show()
            } else {
                // simple demo login: accept any 10-digit number
                // later we can connect this with Firebase Auth
                val intent = Intent(this, MainAppActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
