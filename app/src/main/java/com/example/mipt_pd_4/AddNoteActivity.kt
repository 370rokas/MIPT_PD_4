package com.example.mipt_pd_4

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNoteActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddNoteActivity"
        private const val MAX_NAME_LENGTH = 50
        private const val MAX_CONTENT_LENGTH = 500
    }

    private lateinit var txtNoteName: EditText
    private lateinit var txtNoteContent: EditText
    private lateinit var dbHelper: NoteDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = NoteDatabaseHelper(this)
        txtNoteName = findViewById(R.id.txtNoteName);
        txtNoteContent = findViewById(R.id.txtNoteContent);
        val btnSave = findViewById<Button>(R.id.btnSave);

        btnSave.setOnClickListener {
            Log.d(TAG, "btnSave onClick called")
            saveNote()
        }
    }

    private fun saveNote() {
        Log.d(TAG, "saveNote called")
        // trim kad nebutu tarpu gale
        val name = txtNoteName.text.toString().trim();
        val content = txtNoteContent.text.toString().trim()
        if (!isInputValid(name, content)) {
            return
        }

        // jei insert nepavyko tai greiciausiai toks vardas jau yra
        if (!dbHelper.addNote(name, content)) {
            txtNoteName.error = getString(R.string.error_name_exists)
            return
        }
        Toast.makeText(this, R.string.msg_note_saved, Toast.LENGTH_SHORT).show();
        finish()
    }

    private fun isInputValid(name: String, content: String): Boolean {
        Log.d(TAG, "isInputValid called")
        var isValid = true

        // Vardo tikrinimas
        if (name.isEmpty()) {
            txtNoteName.error = getString(R.string.error_name_empty)
            isValid = false
        } else if (name.length > MAX_NAME_LENGTH) {
            txtNoteName.error = getString(R.string.error_name_too_long)
            isValid = false
        } else if (dbHelper.isNameTaken(name)) {
            txtNoteName.error = getString(R.string.error_name_exists)
            isValid = false
        }

        // Turinio tikrinimas
        if (content.isEmpty()) {
            txtNoteContent.error = getString(R.string.error_content_empty)
            isValid = false
        } else if (content.length > MAX_CONTENT_LENGTH) {
            txtNoteContent.error = getString(R.string.error_content_too_long)
            isValid = false
        }

        return isValid
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        dbHelper.close()
        super.onDestroy()
    }
}
