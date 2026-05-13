package com.example.senzori_kot_sqlite_tema3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SenzoriTelDao {
    // comanda pentru adaugarea unui nou rand de date de la senzori
    @Insert
    suspend fun insert(record: SenzoriTel): Long

    // comanda pentru a aduce toate datele (cele mai noi primele)
    @Query("SELECT * FROM senzori_tel ORDER BY id DESC")
    suspend fun getAllRecords(): List<SenzoriTel>

    // comanda pentru a vedea care este ultimul ID introdus
    @Query("SELECT MAX(id) FROM senzori_tel")
    suspend fun getLastRecordId(): Long?

    // comanda care sterge tot tabelul
    @Query("DELETE FROM senzori_tel")
    suspend fun deleteAll()
}