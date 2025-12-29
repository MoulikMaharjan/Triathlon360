package com.example.triathlon360

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        val etSwim = findViewById<EditText>(R.id.et_swim_goal)
        val etBike = findViewById<EditText>(R.id.et_bike_goal)
        val etRun  = findViewById<EditText>(R.id.et_run_goal)
        val btnSave = findViewById<Button>(R.id.btn_save_goals)

        val prefs = getSharedPreferences("tri_goals", MODE_PRIVATE)

        // load saved goals (or defaults)
        etSwim.setText(prefs.getInt("swim_goal", 2000).toString())
        etBike.setText(prefs.getInt("bike_goal", 50).toString())
        etRun.setText(prefs.getInt("run_goal", 10).toString())

        btnSave.setOnClickListener {
            val swim = etSwim.text.toString().toIntOrNull() ?: 2000
            val bike = etBike.text.toString().toIntOrNull() ?: 50
            val run  = etRun.text.toString().toIntOrNull() ?: 10

            prefs.edit()
                .putInt("swim_goal", swim)
                .putInt("bike_goal", bike)
                .putInt("run_goal", run)
                .apply()

            Toast.makeText(this, "Goals saved ✅", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
