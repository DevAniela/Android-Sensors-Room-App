package com.example.senzori_kot_sqlite_tema3

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// adaugam SensorEventListener pentru a asculta modificarile senzorului
class SensorsFragment : Fragment(), SensorEventListener {

    // declaram variabilele pentru senzor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // declaram elementele de pe ecran
    private lateinit var tvAxaX: TextView
    private lateinit var tvAxaY: TextView
    private lateinit var tvAxaZ: TextView
    private lateinit var btnSalveaza: Button

    // variabile pentru a memora ultimele valori citite
    private var curentX: Float = 0.0f
    private var curentY: Float = 0.0f
    private var curentZ: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // conectam acest cod la designul XML creat anterior
        val view = inflater.inflate(R.layout.fragment_sensors, container, false)

        // gasim elementele dupa ID-ul lor
        tvAxaX = view.findViewById(R.id.tvAxaX)
        tvAxaY = view.findViewById(R.id.tvAxaY)
        tvAxaZ = view.findViewById(R.id.tvAxaZ)
        btnSalveaza = view.findViewById(R.id.btnSalveaza)

        // configuram managerul de senzori al Android
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // definim ce face butonul la apasare
        btnSalveaza.setOnClickListener {
            salveazaInBazaDeDate()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // cand ecranul este activ, pornim ascultarea senzorului
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // cand parasim ecranul, oprim senzorul pentru a nu consuma bateria
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // se activeaza automat de fiecare data cand se misca telefonul
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            curentX = event.values[0]
            curentY = event.values[1]
            curentZ = event.values[2]

            // modificam textul de pe ecran cu noile valori (formatate la 2 zecimale)
            tvAxaX.text = "Axa X: ${"%.2f".format(curentX)}"
            tvAxaY.text = "Axa Y: ${"%.2f".format(curentY)}"
            tvAxaZ.text = "Axa Z: ${"%.2f".format(curentZ)}"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // functie obligatorie pe care nu o folosim
    }

    private fun salveazaInBazaDeDate() {
        // accesarea bazei de date nu are voie sa blocheze interfata (firul principal)
        // folosim Coroutines pentru a executa salvarea in fundal (Dispatchers.IO)
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())

            // cream o noua inregistrare folosind clasa (entitatea) SenzoriTel
            val inregistrareNoua = SenzoriTel(
                id = 0, // se genereaza automat
                timestamp = System.currentTimeMillis(), // timpul curent
                ax = curentX, // accelerometru x
                ay = curentY, // accelerometru y
                az = curentZ, // accelerometru z

                // deocamdata nu citim ceilalti senzori (punem 0.0f)
                gx = 0.0f, gy = 0.0f, gz = 0.0f, // giroscop
                mx = 0.0f, my = 0.0f, mz = 0.0f, // magnetometru
                ox = 0.0f, oy = 0.0f, oz = 0.0f, // orientare
                bar = 0.0f,                      // barometru
                temp = 0.0f                      // temperatura
            )

            // apelam obiectul senzoriTelDao sa salveze obiectul
            db.senzoriTelDao().insert(inregistrareNoua)

            // ne intoarcem pe firul principal pentru a afisa un mesaj de confirmare
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Date salvate cu succes!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}