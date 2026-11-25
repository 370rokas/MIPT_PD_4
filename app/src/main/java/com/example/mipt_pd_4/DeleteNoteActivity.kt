package com.example.mipt_pd_4

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DeleteNoteActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DeleteNoteActivity"
    }

    private lateinit var spnNotes: Spinner
    private lateinit var notes: List<Note>
    private lateinit var dbHelper: NoteDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = NoteDatabaseHelper(this)
        spnNotes = findViewById(R.id.spnNotes);
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        // Spinneriui reikia tik pavadinimu
        notes = dbHelper.getAllNotes()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, notes.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnNotes.adapter = adapter;

        // nera ka trint tai isjungiam mygtuka
        if (notes.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_notes_to_delete, Toast.LENGTH_SHORT).show()
            btnDelete.isEnabled = false
        }

        btnDelete.setOnClickListener {
            Log.d(TAG, "btnDelete onClick called")
            deleteSelectedNote()
        }
    }

    private fun deleteSelectedNote() {
        Log.d(TAG, "deleteSelectedNote called")
        val position = spnNotes.selectedItemPosition
        if (position == AdapterView.INVALID_POSITION || position >= notes.size) {
            Toast.makeText(this, R.string.error_no_selection, Toast.LENGTH_SHORT).show()
            return
        }

        dbHelper.deleteNote(notes[position].id);
        Toast.makeText(this, R.string.msg_note_deleted, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        dbHelper.close()
        super.onDestroy()
    }
}
