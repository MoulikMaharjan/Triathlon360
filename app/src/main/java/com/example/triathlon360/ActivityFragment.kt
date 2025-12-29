package com.example.triathlon360

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class ActivityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        val swimBtn = view.findViewById<Button>(R.id.button_swim)
        val bikeBtn = view.findViewById<Button>(R.id.button_bike)
        val runBtn = view.findViewById<Button>(R.id.button_run)

        swimBtn.setOnClickListener {
            val intent = Intent(requireContext(), SwimSessionActivity::class.java)
            startActivity(intent)
        }

        bikeBtn.setOnClickListener {
            val intent = Intent(requireContext(), BikeSessionActivity::class.java)
            startActivity(intent)
        }

        runBtn.setOnClickListener {
            val intent = Intent(requireContext(), RunSessionActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}
