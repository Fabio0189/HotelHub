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

    fun insertUser(id:Int,email: String, password: String, userType: Int, nome: String, cognome: String) {
        val values = ContentValues().apply {
            put(COLUMN_ID,id)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_TIPO, userType)
            put(COLUMN_NOME, nome)
            put(COLUMN_COGNOME, cognome)
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
    fun getNomeUtente(): String {
        val selectQuery = "SELECT $COLUMN_NOME FROM $TABLE_UTENTE WHERE $COLUMN_EMAIL IS NOT NULL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var nomeUtente = ""

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_NOME)
            if (columnIndex != -1) {
                nomeUtente = cursor.getString(columnIndex)
            }
        }

        cursor.close()
        return nomeUtente
    }

    fun getCognomeUtente(): String {
        val selectQuery = "SELECT $COLUMN_COGNOME FROM $TABLE_UTENTE WHERE $COLUMN_EMAIL IS NOT NULL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var cognomeUtente = ""

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_COGNOME)
            if (columnIndex != -1) {
                cognomeUtente = cursor.getString(columnIndex)
            }
        }

        cursor.close()
        return cognomeUtente
    }

    fun getEmail(): String {
        val selectQuery = "SELECT $COLUMN_EMAIL FROM $TABLE_UTENTE WHERE $COLUMN_EMAIL IS NOT NULL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var email = ""

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_EMAIL)
            if (columnIndex != -1) {
                email = cursor.getString(columnIndex)
            }
        }

        cursor.close()
        return email
    }
    fun getId(): Int {
        val selectQuery = "SELECT $COLUMN_ID FROM $TABLE_UTENTE WHERE $COLUMN_EMAIL IS NOT NULL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var id = -1

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_ID)
            if (columnIndex != -1) {
                id = cursor.getInt(columnIndex)
            }
        }

        cursor.close()
        return id
    }
    fun getPassword(): String {
        val selectQuery = "SELECT $COLUMN_PASSWORD FROM $TABLE_UTENTE WHERE $COLUMN_EMAIL IS NOT NULL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var password = ""

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_PASSWORD)
            if (columnIndex != -1) {
                password = cursor.getString(columnIndex)
            }
        }

        cursor.close()
        return password
    }
    fun updateUserData(id: Int, nome: String, cognome: String, email: String, password: String,) {
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NOME, nome)
            put(COLUMN_COGNOME, cognome)
        }

        val db = writableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        db.update(TABLE_UTENTE, values, selection, selectionArgs)
        db.close()
    }

}
