package com.example.triathlon360

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TrainingHistoryActivity : AppCompatActivity() {

    private lateinit var dao: TrainingSessionDao
    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: TrainingHistoryAdapter

    private var lastDeleted: TrainingSessionEntity? = null
    private var lastDeletedPos = -1

    private val sessions = mutableListOf<TrainingSessionEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_history)

        dao = AppDatabase.getDatabase(this).trainingSessionDao()

        recycler = findViewById(R.id.recycler_history)
        emptyText = findViewById(R.id.text_empty_history)

        adapter = TrainingHistoryAdapter(sessions) { session ->
            openEdit(session)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        setupSwipe()
        loadSessions()
    }

    private fun loadSessions() {
        lifecycleScope.launch {
            val data = dao.getAllSessions()

            sessions.clear()
            sessions.addAll(data)
            adapter.update(sessions)

            if (sessions.isEmpty()) {
                emptyText.visibility = TextView.VISIBLE
                recycler.visibility = RecyclerView.GONE
            } else {
                emptyText.visibility = TextView.GONE
                recycler.visibility = RecyclerView.VISIBLE
            }
        }
    }

    private fun openEdit(session: TrainingSessionEntity) {
        val i = Intent(this, EditSessionActivity::class.java)
        i.putExtra("id", session.id)
        i.putExtra("type", session.type)
        i.putExtra("date", session.date)
        i.putExtra("durationMin", session.durationMin)
        i.putExtra("distanceKm", session.distanceKm)
        i.putExtra("intensity", session.intensity)
        i.putExtra("notes", session.notes)
        startActivity(i)
    }

    private fun setupSwipe() {
        val helper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.adapterPosition
                val session = sessions[pos]

                lastDeleted = session
                lastDeletedPos = pos

                sessions.removeAt(pos)
                adapter.removeAt(pos)

                lifecycleScope.launch {
                    dao.delete(session)
                }

                Snackbar.make(recycler, "Session deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        lifecycleScope.launch {
                            lastDeleted?.let {
                                dao.insert(it)
                                loadSessions()
                            }
                        }
                    }
                    .show()
            }
        })

        helper.attachToRecyclerView(recycler)
    }
}
