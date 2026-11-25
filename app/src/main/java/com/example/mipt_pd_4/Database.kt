package com.example.mipt_pd_4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class NoteDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "NoteDatabaseHelper"
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NOTES = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate called")
        db.execSQL(
            "CREATE TABLE $TABLE_NOTES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                "$COLUMN_CONTENT TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade called: $oldVersion -> $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun getAllNotes(): List<Note> {
        Log.d(TAG, "getAllNotes called")
        val notes = mutableListOf<Note>()
        readableDatabase.query(TABLE_NOTES, null, null, null, null, null, COLUMN_ID).use { cursor ->
            while (cursor.moveToNext()) {
                notes.add(
                    Note(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                    )
                )
            }
        }
        return notes;
    }

    fun addNote(name: String, content: String): Boolean {
        Log.d(TAG, "addNote called: $name")
        // https://developer.android.com/training/data-storage/sqlite
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_CONTENT, content)
        }
        return writableDatabase.insert(TABLE_NOTES, null, values) != -1L
    }

    fun deleteNote(id: Long) {
        Log.d(TAG, "deleteNote called: $id")
        writableDatabase.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(id.toString()));
    }

    fun isNameTaken(name: String): Boolean {
        Log.d(TAG, "isNameTaken called: $name")
        readableDatabase.query(
            TABLE_NOTES, arrayOf(COLUMN_ID), "$COLUMN_NAME = ?", arrayOf(name), null, null, null
        ).use { cursor ->
            return cursor.count > 0
        }
    }
}
