package com.example.triathlon360

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditSessionActivity : AppCompatActivity() {

    private lateinit var dao: TrainingSessionDao
    private var sessionId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_session)

        // ✅ Room DAO
        dao = AppDatabase.getDatabase(this).trainingSessionDao()

        // Views
        val spType = findViewById<Spinner>(R.id.sp_type)
        val etDate = findViewById<EditText>(R.id.input_date)
        val etDuration = findViewById<EditText>(R.id.input_duration)
        val etDistance = findViewById<EditText>(R.id.input_distance)
        val spIntensity = findViewById<Spinner>(R.id.sp_intensity)
        val etNotes = findViewById<EditText>(R.id.input_notes)
        val btnUpdate = findViewById<Button>(R.id.btn_update)

        // Type spinner
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("swim", "bike", "run")
        )
        spType.adapter = typeAdapter

        // Intensity spinner
        val intensityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("EASY", "MOD", "HARD")
        )
        spIntensity.adapter = intensityAdapter

        // ✅ Read intent data
        sessionId = intent.getLongExtra("id", 0L)

        val type = intent.getStringExtra("type") ?: "run"
        val intensity = intent.getStringExtra("intensity") ?: "EASY"

        etDate.setText(intent.getStringExtra("date"))
        etDuration.setText(intent.getIntExtra("durationMin", 0).toString())
        etDistance.setText(intent.getDoubleExtra("distanceKm", 0.0).toString())
        etNotes.setText(intent.getStringExtra("notes"))

        // Set spinner selections
        spType.setSelection(typeAdapter.getPosition(type))
        spIntensity.setSelection(intensityAdapter.getPosition(intensity))

        // ✅ Update button
        btnUpdate.setOnClickListener {

            if (etDate.text.isNullOrBlank() || etDuration.text.isNullOrBlank()) {
                Toast.makeText(this, "Date & duration required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedSession = TrainingSessionEntity(
                id = sessionId,
                type = spType.selectedItem.toString(),
                date = etDate.text.toString().trim(),
                durationMin = etDuration.text.toString().toInt(),
                distanceKm = etDistance.text.toString().toDoubleOrNull() ?: 0.0,
                intensity = spIntensity.selectedItem.toString(),
                notes = etNotes.text.toString().trim()
            )

            lifecycleScope.launch {
                dao.update(updatedSession)
                Toast.makeText(this@EditSessionActivity, "Session updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
