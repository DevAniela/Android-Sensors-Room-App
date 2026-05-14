package com.example.senzori_kot_sqlite_tema3

import androidx.room.Entity
import androidx.room.PrimaryKey

// definim tabelul pentru stocarea datelor gps
@Entity(tableName = "senzori_gps")

data class SenzoriGps(
    // id generat automat pentru fiecare inregistrare
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // momentul inregistrarii in milisecunde
    val timestamp: Long,
    // coordonatele geografice in format double pentru precizie
    val latitudine: Double,
    val longitudine: Double,
    val altitudine: Double,
    // detalii despre deplasare si eroare
    val viteza: Float,
    val eroare: Float,
    // informatii despre numarul de sateliti
    val sats_used: Int,
    val sats_count: Int
)