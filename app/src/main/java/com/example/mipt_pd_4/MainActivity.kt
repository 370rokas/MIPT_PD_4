package com.example.mipt_pd_4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var txtNotes: TextView
    private lateinit var dbHelper: NoteDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = NoteDatabaseHelper(this)
        txtNotes = findViewById(R.id.txtNotes);
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        showNotes()
    }

    private fun showNotes() {
        Log.d(TAG, "showNotes called")
        val notes = dbHelper.getAllNotes();

        // jei nera uzrasu tai rodom tiesiog teksta
        if (notes.isEmpty()) {
            txtNotes.text = getString(R.string.no_notes)
            return
        }

        // sudedam visus uzrasus i viena stringa
        var text = ""
        for (note in notes) {
            text += note.name + "\n" + note.content + "\n\n"
        }
        txtNotes.text = text;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected called: ${item.title}")
        // Meniu pasirinkimai
        return when (item.itemId) {
            R.id.menuAddNote -> {
                startActivity(Intent(this, AddNoteActivity::class.java));
                true
            }
            R.id.menuDeleteNote -> {
                startActivity(Intent(this, DeleteNoteActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        dbHelper.close() // Saugiai uzdarom db kad nebutu dataloss'o
        super.onDestroy()
    }
}
