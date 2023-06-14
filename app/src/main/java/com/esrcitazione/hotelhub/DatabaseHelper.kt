package com.esrcitazione.hotelhub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "myapp.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_UTENTE = "utente"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TIPO = "tipo"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NOME = "nome"
        private const val COLUMN_COGNOME = "cognome"
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTableUtente(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Implementa l'aggiornamento del database se necessario
    }

    private fun createTableUtente(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_UTENTE " +
                "($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TIPO INTEGER, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_NOME TEXT, " +
                "$COLUMN_COGNOME TEXT)"

        db.execSQL(createTableQuery)
    }

    fun insertUser(email: String, password: String, userType: Int) {
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_TIPO, userType)
        }

        val db = writableDatabase
        db.insert(TABLE_UTENTE, null, values)
        db.close()
    }

    fun checkUserLoggedIn(): Boolean {
        if (!tableExists(TABLE_UTENTE)) {
            return false
        }

        val selectQuery = "SELECT * FROM $TABLE_UTENTE"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        val isLoggedIn = cursor.count > 0
        cursor.close()
        return isLoggedIn
    }

    fun getUserType(): Int {
        val selectQuery = "SELECT $COLUMN_TIPO FROM $TABLE_UTENTE WHERE $COLUMN_EMAIL IS NOT NULL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var userType = -1 // Valore predefinito per indicare un errore o un valore non valido

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_TIPO)
            if (columnIndex != -1) {
                userType = cursor.getInt(columnIndex)
            }
        }

        cursor.close()
        return userType
    }

    fun deleteTabella() {
        val db = writableDatabase
        db.delete(TABLE_UTENTE, null, null)
        db.close()
    }

    private fun tableExists(tableName: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        )
        val tableExists = cursor.moveToFirst()
        cursor.close()
        return tableExists
    }
}
