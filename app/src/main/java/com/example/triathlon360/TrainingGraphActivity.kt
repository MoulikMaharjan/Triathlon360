package com.example.triathlon360

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrainingGraphActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_graph)

        val titleText = findViewById<TextView>(R.id.text_graph_title)
        val subtitleText = findViewById<TextView>(R.id.text_graph_subtitle)
        val legendText = findViewById<TextView>(R.id.text_graph_legend)
        val barContainer = findViewById<LinearLayout>(R.id.bar_container)

        // you can send this extra from Dashboard or More menu later
        val graphType = intent.getStringExtra("graph_type") // "swim", "bike", "run", or null

        // adjust title based on filter
        when (graphType) {
            "swim" -> titleText.text = "swim training graph"
            "bike" -> titleText.text = "bike training graph"
            "run"  -> titleText.text = "run training graph"
            else   -> titleText.text = "training history graph"
        }

        subtitleText.text = "loading graph..."
        legendText.text = "bar height = distance per session (m for swim, km for bike/run)"

        barContainer.removeAllViews()

        // read up to 50 sessions, oldest to newest (so graph goes left→right in time)
        db.collection("sessions")
            .orderBy("created_at", Query.Direction.ASCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    subtitleText.text = "no sessions found"
                    return@addOnSuccessListener
                }

                val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())

                // list of (distance, labelDate)
                val points = mutableListOf<Pair<Double, String>>()

                for (doc in snapshot.documents) {
                    val type = doc.getString("type") ?: continue

                    // if graphType set ("swim"/"bike"/"run"), skip other types
                    if (!graphType.isNullOrBlank() && type != graphType) {
                        continue
                    }

                    val createdAt = doc.getLong("created_at") ?: 0L
                    val dateLabel = if (createdAt > 0L) {
                        sdf.format(Date(createdAt))
                    } else {
                        "--"
                    }

                    when (type) {
                        "swim" -> {
                            val distStr = doc.getString("distance_m") ?: "0"
                            val dist = distStr.toDoubleOrNull() ?: 0.0
                            if (dist > 0.0) {
                                points.add(Pair(dist, dateLabel))
                            }
                        }
                        "bike" -> {
                            val distStr = doc.getString("distance_km") ?: "0"
                            val dist = distStr.toDoubleOrNull() ?: 0.0
                            if (dist > 0.0) {
                                points.add(Pair(dist, dateLabel))
                            }
                        }
                        "run" -> {
                            val distStr = doc.getString("distance_km") ?: "0"
                            val dist = distStr.toDoubleOrNull() ?: 0.0
                            if (dist > 0.0) {
                                points.add(Pair(dist, dateLabel))
                            }
                        }
                    }
                }

                if (points.isEmpty()) {
                    subtitleText.text = "no sessions for this filter"
                    return@addOnSuccessListener
                }

                subtitleText.text = "distance per session (${points.size} sessions)"

                // find max distance to scale bar heights
                val maxValue = points.maxOf { it.first }
                if (maxValue <= 0.0) {
                    subtitleText.text = "no valid distances to plot"
                    return@addOnSuccessListener
                }

                val maxHeightPx = dpToPx(160) // max bar height on screen
                val barColor = ContextCompat.getColor(this, R.color.tri_neon2)
                val labelColor = ContextCompat.getColor(this, R.color.tri_text)

                for ((value, dateLabel) in points) {
                    // fraction of max
                    val fraction = (value / maxValue).coerceIn(0.0, 1.0)
                    val barHeightPx = (maxHeightPx * fraction).toInt().coerceAtLeast(dpToPx(4))

                    // each bar group (value, bar, date) in a vertical layout
                    val columnLayout = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            dpToPx(40),
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ).apply {
                            setMargins(dpToPx(4), 0, dpToPx(4), 0)
                        }
                    }

                    // small text above bar: distance
                    val valueLabel = TextView(this).apply {
                        text = String.format(Locale.getDefault(), "%.1f", value)
                        textSize = 10f
                        setTextColor(labelColor)
                        gravity = Gravity.CENTER
                    }

                    // actual bar view
                    val barView = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            dpToPx(20),
                            barHeightPx
                        ).apply {
                            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                            topMargin = dpToPx(4)
                            bottomMargin = dpToPx(4)
                        }
                        setBackgroundColor(barColor)
                    }

                    // date label below bar
                    val dateText = TextView(this).apply {
                        text = dateLabel
                        textSize = 10f
                        setTextColor(labelColor)
                        gravity = Gravity.CENTER
                    }

                    columnLayout.addView(valueLabel)
                    columnLayout.addView(barView)
                    columnLayout.addView(dateText)

                    barContainer.addView(columnLayout)
                }
            }
            .addOnFailureListener {
                subtitleText.text = "failed to load data"
            }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale).toInt()
    }
}
