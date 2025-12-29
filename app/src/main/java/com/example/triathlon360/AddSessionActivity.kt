package com.example.triathlon360

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSessionActivity : AppCompatActivity() {

    private val dao by lazy {
        AppDatabase.getDatabase(this).trainingSessionDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_session)

        // Views
        val spType = findViewById<Spinner>(R.id.sp_type)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etDuration = findViewById<EditText>(R.id.et_duration)
        val etDistance = findViewById<EditText>(R.id.et_distance)
        val spIntensity = findViewById<Spinner>(R.id.sp_intensity)
        val etNotes = findViewById<EditText>(R.id.et_notes)
        val btnSave = findViewById<Button>(R.id.btn_save_session)

        // Type spinner
        spType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("swim", "bike", "run")
        )

        // Intensity spinner
        spIntensity.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("EASY", "MOD", "HARD")
        )

        btnSave.setOnClickListener {

            val type = spType.selectedItem.toString()
            val date = etDate.text.toString().trim()
            val duration = etDuration.text.toString().toIntOrNull() ?: 0
            val distanceKm = etDistance.text.toString().toDoubleOrNull() ?: 0.0
            val intensity = spIntensity.selectedItem.toString()
            val notes = etNotes.text.toString().trim()

            if (date.isEmpty() || duration <= 0) {
                Toast.makeText(this, "Enter valid date and duration", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val session = TrainingSessionEntity(
                type = type,
                date = date,
                durationMin = duration,
                distanceKm = distanceKm,
                intensity = intensity,
                notes = notes
            )

            lifecycleScope.launch(Dispatchers.IO) {
                dao.insert(session)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddSessionActivity,
                        "Session saved",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }
}
