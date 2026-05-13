package com.example.senzori_kot_sqlite_tema3

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// listam toate tabelele (entities) si versiunea bazei de date
@Database(entities = [SenzoriTel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // declaram DAO-ul
    abstract fun senzoriTelDao(): SenzoriTelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // metoda care ne da instanta bazei de date
        fun getDatabase(context: Context): AppDatabase {

            // daca exista deja, o returnam
            return INSTANCE ?: synchronized(this) {

                // daca nu exista, o construim acum
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "senzori_db" // numele fisierului bazei de date pe telefon
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}