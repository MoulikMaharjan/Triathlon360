package com.example.triathlon360

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        // --- Training graphs (all) ---
        view.findViewById<TextView?>(R.id.text_graphs)?.setOnClickListener {
            startActivity(Intent(requireContext(), TrainingGraphActivity::class.java))
        }

        // --- Training graphs (filtered) ---
        view.findViewById<TextView?>(R.id.text_graph_swim)?.setOnClickListener {
            val i = Intent(requireContext(), TrainingGraphActivity::class.java)
            i.putExtra("graph_type", "swim")
            startActivity(i)
        }

        view.findViewById<TextView?>(R.id.text_graph_bike)?.setOnClickListener {
            val i = Intent(requireContext(), TrainingGraphActivity::class.java)
            i.putExtra("graph_type", "bike")
            startActivity(i)
        }

        view.findViewById<TextView?>(R.id.text_graph_run)?.setOnClickListener {
            val i = Intent(requireContext(), TrainingGraphActivity::class.java)
            i.putExtra("graph_type", "run")
            startActivity(i)
        }

        // --- Weekly load chart ---
        // ✅ IMPORTANT:
        // Use the ID that EXISTS in your fragment_more.xml.
        // If your XML id is text_training_load, keep this line.
        // If your XML id is text_weekly_load, change it here.
        view.findViewById<TextView?>(R.id.text_training_load)?.setOnClickListener {
            startActivity(Intent(requireContext(), WeeklyLoadActivity::class.java))
        }

        // --- Training history ---
        view.findViewById<TextView?>(R.id.text_view_history)?.setOnClickListener {
            startActivity(Intent(requireContext(), TrainingHistoryActivity::class.java))
        }

        // --- Goals ---
        view.findViewById<TextView?>(R.id.text_goals)?.setOnClickListener {
            startActivity(Intent(requireContext(), GoalsActivity::class.java))
        }

        return view
    }
}
