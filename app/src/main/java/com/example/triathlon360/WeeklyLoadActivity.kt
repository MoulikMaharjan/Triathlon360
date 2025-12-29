package com.example.triathlon360

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class WeeklyLoadActivity : AppCompatActivity() {

    private val dao by lazy {
        AppDatabase.getDatabase(this).trainingSessionDao()
    }

    private val keyFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_load)

        val title = findViewById<TextView>(R.id.text_weekly_title)
        val subtitle = findViewById<TextView>(R.id.text_weekly_subtitle)
        val chart = findViewById<BarChart>(R.id.bar_chart_weekly)

        title.text = "Weekly Training Load"
        subtitle.text = "Last 7 days"

        setupChart(chart)
        loadWeekly(chart, subtitle)
    }

    private fun setupChart(chart: BarChart) {
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setScaleEnabled(false)

        chart.axisLeft.apply {
            axisMinimum = 0f
            textColor = Color.WHITE
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.roundToInt()}m"
                }
            }
        }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.WHITE
            granularity = 1f
            setDrawGridLines(false)
        }

        chart.legend.textColor = Color.WHITE
    }

    private fun loadWeekly(chart: BarChart, subtitle: TextView) {
        lifecycleScope.launch {
            val sessions = dao.getAllSessions()
            val days = last7Days()

            val swim = IntArray(7)
            val bike = IntArray(7)
            val run = IntArray(7)

            for (s in sessions) {
                val idx = days.indexOfFirst { it.first == s.date }
                if (idx == -1) continue

                when (s.type.lowercase()) {
                    "swim" -> swim[idx] += s.durationMin
                    "bike" -> bike[idx] += s.durationMin
                    "run" -> run[idx] += s.durationMin
                }
            }

            val entries = ArrayList<BarEntry>()
            for (i in 0..6) {
                entries.add(
                    BarEntry(
                        i.toFloat(),
                        floatArrayOf(
                            swim[i].toFloat(),
                            bike[i].toFloat(),
                            run[i].toFloat()
                        )
                    )
                )
            }

            val dataSet = BarDataSet(entries, "Minutes").apply {
                setDrawValues(false)
                colors = listOf(
                    Color.parseColor("#00E5FF"),
                    Color.parseColor("#7C4DFF"),
                    Color.parseColor("#00E676")
                )
                stackLabels = arrayOf("Swim", "Bike", "Run")
            }

            chart.data = BarData(dataSet).apply {
                barWidth = 0.65f
            }

            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return days[value.toInt()].second
                }
            }

            chart.invalidate()
            subtitle.text = "Total ${swim.sum() + bike.sum() + run.sum()} min"
        }
    }

    private fun last7Days(): List<Pair<String, String>> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)

        val list = mutableListOf<Pair<String, String>>()
        repeat(7) {
            list.add(
                keyFmt.format(cal.time) to dayFmt.format(cal.time)
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }
}
