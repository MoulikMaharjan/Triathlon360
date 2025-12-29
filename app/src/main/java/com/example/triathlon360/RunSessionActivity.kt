package com.example.triathlon360

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RunSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_session)

        val distEdit = findViewById<EditText>(R.id.edit_run_distance)
        val timeEdit = findViewById<EditText>(R.id.edit_run_time)
        val paceEdit = findViewById<EditText>(R.id.edit_run_pace)
        val rpeEdit = findViewById<EditText>(R.id.edit_run_rpe)
        val notesEdit = findViewById<EditText>(R.id.edit_run_notes)
        val saveBtn = findViewById<Button>(R.id.button_save_run)

        val db = Firebase.firestore

        saveBtn.setOnClickListener {
            val distance = distEdit.text.toString().trim()
            val time = timeEdit.text.toString().trim()
            val pace = paceEdit.text.toString().trim()
            val rpe = rpeEdit.text.toString().trim()
            val notes = notesEdit.text.toString().trim()

            if (distance.isBlank() || time.isBlank()) {
                Toast.makeText(this, "please enter distance and time", Toast.LENGTH_SHORT).show()
            } else {
                // 1) local history
                val entry = buildString {
                    append("run – ")
                    append(distance).append(" km in ").append(time).append(" min")
                    if (pace.isNotBlank()) append(" (pace ").append(pace).append(" min/km)")
                    if (rpe.isNotBlank()) append(", rpe ").append(rpe)
                    if (notes.isNotBlank()) append("\n  note: ").append(notes)
                }

                val prefs = getSharedPreferences("training_prefs", MODE_PRIVATE)
                val oldHistory = prefs.getString("history_text", "")
                val newHistory = if (oldHistory.isNullOrBlank()) entry else oldHistory + "\n\n" + entry
                prefs.edit().putString("history_text", newHistory).apply()

                // 2) cloud save
                val runData = hashMapOf(
                    "type" to "run",
                    "distance_km" to distance,
                    "time_min" to time,
                    "pace_min_per_km" to pace,
                    "rpe" to rpe,
                    "notes" to notes,
                    "created_at" to System.currentTimeMillis()
                )

                db.collection("sessions")
                    .add(runData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "run session saved (cloud + local)", Toast.LENGTH_SHORT).show()
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
