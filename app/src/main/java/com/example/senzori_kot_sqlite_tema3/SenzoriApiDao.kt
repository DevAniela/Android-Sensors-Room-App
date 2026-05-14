package com.example.senzori_kot_sqlite_tema3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// interfata pentru accesarea tabelului de date api
@Dao
interface SenzoriApiDao {
    // insereaza un nou set de date preluate de la server
    @Insert
    suspend fun insert(record: SenzoriApi): Long

    // preia toate datele descarcate de pe server
    @Query("SELECT * FROM senzori_api ORDER BY id DESC")
    suspend fun getAllRecords(): List<SenzoriApi>

    // curata istoricul datelor de retea
    @Query("DELETE FROM senzori_api")
    suspend fun deleteAll()
}