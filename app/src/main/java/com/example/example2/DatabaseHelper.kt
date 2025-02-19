package com.example.example2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun insertUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username=?", arrayOf(username))
        if (cursor.count > 0) {
            cursor.close()
            return false
        }
        db.execSQL("INSERT INTO users (username, password) VALUES (?, ?)", arrayOf(username, password))
        return true
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
    fun isUserExists(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}