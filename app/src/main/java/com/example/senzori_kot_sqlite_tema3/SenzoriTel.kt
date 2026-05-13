package com.example.senzori_kot_sqlite_tema3

import androidx.room.Entity
import androidx.room.PrimaryKey

// ii spunem telefonului sa creeze un tabel cu numele senzori_tel
@Entity(tableName = "senzori_tel")
data class SenzoriTel(
    // ID-ul este generat automat pentru fiecare rand
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // campul pentru timp (va stoca milisecunde)
    val timestamp: Long,

    // senzorii (float este standardul în Android pentru datele care vin cu zecimale de la senzori)
    val ax: Float, val ay: Float, val az: Float, // accelerometru
    val gx: Float, val gy: Float, val gz: Float, // giroscop
    val mx: Float, val my: Float, val mz: Float, // magnetometru
    val ox: Float, val oy: Float, val oz: Float, // orientare
    val bar: Float,                              // barometru
    val temp: Float                              // temperatura
)