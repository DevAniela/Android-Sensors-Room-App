package com.example.senzori_kot_sqlite_tema3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// interfata pentru manipularea datelor din tabelul gps
@Dao
interface SenzoriGpsDao {
    // salveaza o inregistrare gps noua
    @Insert
    suspend fun insert(record: SenzoriGps): Long

    // returneaza istoricul locatiilor sortat descrescator dupa id
    @Query("SELECT * FROM senzori_gps ORDER BY id DESC")
    suspend fun getAllRecords(): List<SenzoriGps>

    // sterge toate coordonatele salvate
    @Query("DELETE FROM senzori_gps")
    suspend fun deleteAll()
}