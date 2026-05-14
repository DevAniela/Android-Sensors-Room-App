package com.example.senzori_kot_sqlite_tema3

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// definim baza de date si includem cele trei tabele
@Database(entities = [SenzoriTel::class, SenzoriGps::class, SenzoriApi::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // metode abstracte pentru a obtine acces la fiecare dao in parte
    abstract fun senzoriTelDao(): SenzoriTelDao
    abstract fun senzoriGpsDao(): SenzoriGpsDao
    abstract fun senzoriApiDao(): SenzoriApiDao

    companion object {
        // variabila INSTANCE mentine o singura instanta a bazei de date pentru a evita conflictele
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // functia care returneaza instanta bazei de date
        fun getDatabase(context: Context): AppDatabase {
            // daca instanta exista deja o returnam direct pentru a economisi memorie
            return INSTANCE ?: synchronized(this) {
                // daca este nula construim baza de date de la zero
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "senzori_db"
                )
                    // permite stergerea si recrearea bazei de date atunci cand modificam structura tabelelor
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}