package com.example.triathlon360

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SwimSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swim_session)

        val distanceEdit = findViewById<EditText>(R.id.edit_distance)
        val timeEdit = findViewById<EditText>(R.id.edit_time)
        val poolEdit = findViewById<EditText>(R.id.edit_pool_length)
        val rpeEdit = findViewById<EditText>(R.id.edit_rpe)
        val notesEdit = findViewById<EditText>(R.id.edit_notes)
        val saveButton = findViewById<Button>(R.id.button_save_swim)

        val db = Firebase.firestore   // firestore instance

        saveButton.setOnClickListener {
            val distance = distanceEdit.text.toString().trim()
            val time = timeEdit.text.toString().trim()
            val pool = poolEdit.text.toString().trim()
            val rpe = rpeEdit.text.toString().trim()
            val notes = notesEdit.text.toString().trim()

            if (distance.isBlank() || time.isBlank()) {
                Toast.makeText(this, "please enter distance and time", Toast.LENGTH_SHORT).show()
            } else {
                // 1) local history (what we already had)
                val entry = buildString {
                    append("swim – ")
                    append(distance).append(" m in ").append(time).append(" min")
                    if (pool.isNotBlank()) append(" (pool ").append(pool).append(" m)")
                    if (rpe.isNotBlank()) append(", rpe ").append(rpe)
                    if (notes.isNotBlank()) append("\n  note: ").append(notes)
                }

                val prefs = getSharedPreferences("training_prefs", MODE_PRIVATE)
                val oldHistory = prefs.getString("history_text", "")
                val newHistory = if (oldHistory.isNullOrBlank()) entry else oldHistory + "\n\n" + entry
                prefs.edit().putString("history_text", newHistory).apply()

                // 2) save to firestore
                val swimData = hashMapOf(
                    "type" to "swim",
                    "distance_m" to distance,
                    "time_min" to time,
                    "pool_length_m" to pool,
                    "rpe" to rpe,
                    "notes" to notes,
                    "created_at" to System.currentTimeMillis()
                )

                db.collection("sessions")
                    .add(swimData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "swim session saved (cloud + local)", Toast.LENGTH_SHORT).show()
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
