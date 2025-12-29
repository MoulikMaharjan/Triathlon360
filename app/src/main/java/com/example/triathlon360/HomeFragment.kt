package com.example.triathlon360

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // summary text views
        val swimSummaryText = view.findViewById<TextView>(R.id.text_swim_summary)
        val bikeSummaryText = view.findViewById<TextView>(R.id.text_bike_summary)
        val runSummaryText  = view.findViewById<TextView>(R.id.text_run_summary)

        val swimHintText = view.findViewById<TextView>(R.id.text_swim_hint)
        val bikeHintText = view.findViewById<TextView>(R.id.text_bike_hint)
        val runHintText  = view.findViewById<TextView>(R.id.text_run_hint)

        // ring text views (inside the circular frames)
        val ringSwim = view.findViewById<TextView>(R.id.text_ring_swim)
        val ringBike = view.findViewById<TextView>(R.id.text_ring_bike)
        val ringRun  = view.findViewById<TextView>(R.id.text_ring_run)

        // set default text
        swimSummaryText.text = "no swim data yet"
        bikeSummaryText.text = "no bike data yet"
        runSummaryText.text  = "no run data yet"

        swimHintText.text = "tip: log your first swim session today"
        bikeHintText.text = "tip: add at least one bike ride this week"
        runHintText.text  = "tip: schedule an easy run tomorrow"

        ringSwim.text = "0%"
        ringBike.text = "0%"
        ringRun.text  = "0%"

        // load data from Firestore
        loadLatestSessions(
            swimSummaryText,
            bikeSummaryText,
            runSummaryText,
            swimHintText,
            bikeHintText,
            runHintText
        )

        loadWeeklyRings(ringSwim, ringBike, ringRun)

        return view
    }

    /**
     * Load the latest swim, bike, and run sessions to show a simple summary in the cards.
     */
    private fun loadLatestSessions(
        swimSummary: TextView,
        bikeSummary: TextView,
        runSummary: TextView,
        swimHint: TextView,
        bikeHint: TextView,
        runHint: TextView
    ) {
        val db = Firebase.firestore

        db.collection("sessions")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .limit(30) // look at last 30 entries, enough to find last of each type
            .get()
            .addOnSuccessListener { result ->

                var foundSwim = false
                var foundBike = false
                var foundRun  = false

                for (doc in result) {
                    val type = doc.getString("type") ?: ""

                    when (type) {
                        "swim" -> if (!foundSwim) {
                            val dist = doc.getString("distance_m") ?: ""
                            val time = doc.getString("time_min") ?: ""
                            swimSummary.text = "last swim: $dist m in $time min"
                            swimHint.text = "keep building your swim endurance"
                            foundSwim = true
                        }

                        "bike" -> if (!foundBike) {
                            val dist = doc.getString("distance_km") ?: ""
                            val time = doc.getString("time_min") ?: ""
                            bikeSummary.text = "last bike: $dist km in $time min"
                            bikeHint.text = "aim for a smooth cadence today"
                            foundBike = true
                        }

                        "run" -> if (!foundRun) {
                            val dist = doc.getString("distance_km") ?: ""
                            val time = doc.getString("time_min") ?: ""
                            runSummary.text = "last run: $dist km in $time min"
                            runHint.text = "focus on relaxed breathing"
                            foundRun = true
                        }
                    }

                    if (foundSwim && foundBike && foundRun) {
                        break
                    }
                }

                // if some are still not found, defaults from onCreateView remain
            }
            .addOnFailureListener { e ->
                context?.let {
                    Toast.makeText(it, "Error loading dashboard: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Compute weekly totals (last 7 days) and map them to a percentage ring.
     * You can adjust the goals as you like.
     */
    private fun loadWeeklyRings(
        ringSwim: TextView,
        ringBike: TextView,
        ringRun: TextView
    ) {
        val db = Firebase.firestore
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 7L * 24L * 60L * 60L * 1000L

        db.collection("sessions")
            .whereGreaterThanOrEqualTo("created_at", sevenDaysAgo)
            .get()
            .addOnSuccessListener { result ->
                var swimTotalMeters = 0.0
                var bikeTotalKm = 0.0
                var runTotalKm = 0.0

                for (doc in result) {
                    val type = doc.getString("type") ?: ""

                    when (type) {
                        "swim" -> {
                            val distStr = doc.getString("distance_m") ?: "0"
                            val dist = distStr.toDoubleOrNull() ?: 0.0
                            swimTotalMeters += dist
                        }
                        "bike" -> {
                            val distStr = doc.getString("distance_km") ?: "0"
                            val dist = distStr.toDoubleOrNull() ?: 0.0
                            bikeTotalKm += dist
                        }
                        "run" -> {
                            val distStr = doc.getString("distance_km") ?: "0"
                            val dist = distStr.toDoubleOrNull() ?: 0.0
                            runTotalKm += dist
                        }
                    }
                }

                // simple weekly goals (tweak these to your real goals)
                val swimGoalMeters = 10000.0   // 10 km swim
                val bikeGoalKm     = 100.0     // 100 km bike
                val runGoalKm      = 30.0      // 30 km run

                val swimPercent = ((swimTotalMeters / swimGoalMeters) * 100.0).coerceIn(0.0, 100.0)
                val bikePercent = ((bikeTotalKm / bikeGoalKm) * 100.0).coerceIn(0.0, 100.0)
                val runPercent  = ((runTotalKm / runGoalKm) * 100.0).coerceIn(0.0, 100.0)

                ringSwim.text = "${swimPercent.toInt()}%"
                ringBike.text = "${bikePercent.toInt()}%"
                ringRun.text  = "${runPercent.toInt()}%"
            }
            .addOnFailureListener { e ->
                context?.let {
                    Toast.makeText(it, "Error loading weekly rings: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
