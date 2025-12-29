package com.example.triathlon360

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrainingHistoryAdapter(
    private var items: MutableList<TrainingSessionEntity>,
    private val onItemClick: (TrainingSessionEntity) -> Unit
) : RecyclerView.Adapter<TrainingHistoryAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val stripe: View = v.findViewById(R.id.view_stripe)
        val number: TextView = v.findViewById(R.id.text_number)
        val title: TextView = v.findViewById(R.id.text_title)
        val subtitle: TextView = v.findViewById(R.id.text_subtitle)
        val tag: TextView = v.findViewById(R.id.text_tag)
        val notes: TextView = v.findViewById(R.id.text_notes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training_session, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val s = items[position]

        h.number.text = (position + 1).toString()

        val typeTitle = when (s.type.lowercase()) {
            "swim" -> "Swim"
            "bike" -> "Bike"
            "run"  -> "Run"
            else   -> s.type
        }

        val stripeColor = when (s.type.lowercase()) {
            "swim" -> Color.parseColor("#00E5FF")
            "bike" -> Color.parseColor("#7C4DFF")
            "run"  -> Color.parseColor("#00E676")
            else   -> Color.parseColor("#FFEA00")
        }
        h.stripe.setBackgroundColor(stripeColor)

        // ✅ FIXED: distanceKm
        h.title.text = "$typeTitle • ${formatDistance(s)}"
        h.subtitle.text = "${s.date} • ${s.durationMin} min"

        h.tag.text = s.intensity.uppercase()
        val pillColor = when (s.intensity.uppercase()) {
            "EASY" -> Color.parseColor("#00E676")
            "MOD", "MODERATE" -> Color.parseColor("#FFEA00")
            "HARD" -> Color.parseColor("#FF5252")
            else -> Color.parseColor("#00E5FF")
        }
        h.tag.setBackgroundColor(pillColor)
        h.tag.setTextColor(Color.parseColor("#111111"))

        if (s.notes.isBlank()) {
            h.notes.visibility = View.GONE
        } else {
            h.notes.visibility = View.VISIBLE
            h.notes.text = "Notes: ${s.notes}"
        }

        h.itemView.setOnClickListener { onItemClick(s) }
    }

    fun update(newItems: List<TrainingSessionEntity>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun removeAt(pos: Int) {
        items.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, items.size)
    }

    fun getItemAt(pos: Int): TrainingSessionEntity = items[pos]

    // ===========================
    // ✅ DISTANCE FORMATTER (FIXED)
    // ===========================
    private fun formatDistance(s: TrainingSessionEntity): String {
        return when (s.type.lowercase()) {
            "swim" -> "${(s.distanceKm * 1000).toInt()} m"   // km → meters
            "bike", "run" -> trim1(s.distanceKm) + " km"
            else -> s.distanceKm.toString()
        }
    }

    private fun trim1(x: Double): String {
        val whole = x.toInt()
        return if (x == whole.toDouble()) whole.toString()
        else String.format("%.1f", x)
    }
}
