package com.example.senzori_kot_sqlite_tema3

import androidx.room.Entity
import androidx.room.PrimaryKey

// definim tabelul pentru traficul de retea preluat de pe serverul timf
@Entity(tableName = "senzori_api")

data class SenzoriApi(
    // cheia primara unica
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // timpul inregistrarii
    val timestamp: Long,

    // cantitatea de date primite si trimise in bytes
    val eth0_rx_bytes: Long,
    val eth0_tx_bytes: Long,

    // numarul de pachete primite si trimise
    val eth0_rx_pkts: Long,
    val eth0_tx_pkts: Long
)