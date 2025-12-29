package com.example.triathlon360

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class BikeSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_session)

        val distEdit = findViewById<EditText>(R.id.edit_bike_distance)
        val timeEdit = findViewById<EditText>(R.id.edit_bike_time)
        val speedEdit = findViewById<EditText>(R.id.edit_bike_speed)
        val rpeEdit = findViewById<EditText>(R.id.edit_bike_rpe)
        val notesEdit = findViewById<EditText>(R.id.edit_bike_notes)
        val saveBtn = findViewById<Button>(R.id.button_save_bike)

        val db = Firebase.firestore

        saveBtn.setOnClickListener {
            val distance = distEdit.text.toString().trim()
            val time = timeEdit.text.toString().trim()
            val speed = speedEdit.text.toString().trim()
            val rpe = rpeEdit.text.toString().trim()
            val notes = notesEdit.text.toString().trim()

            if (distance.isBlank() || time.isBlank()) {
                Toast.makeText(this, "please enter distance and time", Toast.LENGTH_SHORT).show()
            } else {
                // 1) local history
                val entry = buildString {
                    append("bike – ")
                    append(distance).append(" km in ").append(time).append(" min")
                    if (speed.isNotBlank()) append(" (avg ").append(speed).append(" km/h)")
                    if (rpe.isNotBlank()) append(", rpe ").append(rpe)
                    if (notes.isNotBlank()) append("\n  note: ").append(notes)
                }

                val prefs = getSharedPreferences("training_prefs", MODE_PRIVATE)
                val oldHistory = prefs.getString("history_text", "")
                val newHistory = if (oldHistory.isNullOrBlank()) entry else oldHistory + "\n\n" + entry
                prefs.edit().putString("history_text", newHistory).apply()

                // 2) cloud save
                val bikeData = hashMapOf(
                    "type" to "bike",
                    "distance_km" to distance,
                    "time_min" to time,
                    "avg_speed_kmh" to speed,
                    "rpe" to rpe,
                    "notes" to notes,
                    "created_at" to System.currentTimeMillis()
                )

                db.collection("sessions")
                    .add(bikeData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "bike session saved (cloud + local)", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "saved locally, cloud error: ${e.message}", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
        }
    }
}
