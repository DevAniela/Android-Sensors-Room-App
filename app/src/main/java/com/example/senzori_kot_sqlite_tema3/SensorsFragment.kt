import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.senzori_kot_sqlite_tema3.AppDatabase
import com.example.senzori_kot_sqlite_tema3.R
import com.example.senzori_kot_sqlite_tema3.SenzoriGps
import com.example.senzori_kot_sqlite_tema3.SenzoriTel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SensorsFragment : Fragment(), SensorEventListener, LocationListener {

    // declaram variabilele pentru senzori
    private lateinit var sensorManager: SensorManager
    private var ax = 0f; private var ay = 0f; private var az = 0f
    private var gx = 0f; private var gy = 0f; private var gz = 0f
    private var mx = 0f; private var my = 0f; private var mz = 0f
    private var ox = 0f; private var oy = 0f; private var oz = 0f
    private var bar = 0f; private var temp = 0f

    // declaram variabilele pentru GPS
    private lateinit var locationManager: LocationManager
    private var lat = 0.0; private var lon = 0.0; private var alt = 0.0
    private var viteza = 0f; private var eroare = 0f
    private var satsUsed = 0; private var satsCount = 0

    // declaram elementele UI
    private lateinit var tvAccel: TextView
    private lateinit var tvGyro: TextView
    private lateinit var tvMag: TextView
    private lateinit var tvOrient: TextView
    private lateinit var tvBaro: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvGpsLatLon: TextView
    private lateinit var tvGpsAltSpeed: TextView
    private lateinit var tvGpsErrSats: TextView
    private lateinit var btnSalveazaToate: Button

    // cerem permisiunea de locatie cand se deschide ecranul
    private val cerePermisiuneaGps = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { primita ->
        if (primita) pornesteGps() else Toast.makeText(context, "GPS-ul nu va funcționa fără locație", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sensors, container, false)

        // initializare UI
        tvAccel = view.findViewById(R.id.tvAccel)
        tvGyro = view.findViewById(R.id.tvGyro)
        tvMag = view.findViewById(R.id.tvMag)
        tvOrient = view.findViewById(R.id.tvOrient)
        tvBaro = view.findViewById(R.id.tvBaro)
        tvTemp = view.findViewById(R.id.tvTemp)
        tvGpsLatLon = view.findViewById(R.id.tvGpsLatLon)
        tvGpsAltSpeed = view.findViewById(R.id.tvGpsAltSpeed)
        tvGpsErrSats = view.findViewById(R.id.tvGpsErrSats)
        btnSalveazaToate = view.findViewById(R.id.btnSalveazaToate)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnSalveazaToate.setOnClickListener { salveazaInBazaDeDate() }

        // cerem permisiunea de locatie la deschidere
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cerePermisiuneaGps.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            pornesteGps()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // inregistram toti senzorii (daca telefonul ii are)
        val senzori = listOf(
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_ORIENTATION, Sensor.TYPE_PRESSURE, Sensor.TYPE_AMBIENT_TEMPERATURE
        )
        for (tip in senzori) {
            sensorManager.getDefaultSensor(tip)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }

    private fun pornesteGps() {
        try {
            // Cere update-uri de locatie la fiecare secunda (1000 ms) sau la minim 1 metru miscare
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // logica de citire hardware
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> { ax = event.values[0]; ay = event.values[1]; az = event.values[2]; tvAccel.text = "Accelerometru (x,y,z): ${"%.2f".format(ax)}, ${"%.2f".format(ay)}, ${"%.2f".format(az)}" }
            Sensor.TYPE_GYROSCOPE -> { gx = event.values[0]; gy = event.values[1]; gz = event.values[2]; tvGyro.text = "Giroscop (x,y,z): ${"%.2f".format(gx)}, ${"%.2f".format(gy)}, ${"%.2f".format(gz)}" }
            Sensor.TYPE_MAGNETIC_FIELD -> { mx = event.values[0]; my = event.values[1]; mz = event.values[2]; tvMag.text = "Magnetometru (x,y,z): ${"%.2f".format(mx)}, ${"%.2f".format(my)}, ${"%.2f".format(mz)}" }
            Sensor.TYPE_ORIENTATION -> { ox = event.values[0]; oy = event.values[1]; oz = event.values[2]; tvOrient.text = "Orientare (x,y,z): ${"%.2f".format(ox)}, ${"%.2f".format(oy)}, ${"%.2f".format(oz)}" }
            Sensor.TYPE_PRESSURE -> { bar = event.values[0]; tvBaro.text = "Barometru: ${"%.2f".format(bar)} hPa" }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> { temp = event.values[0]; tvTemp.text = "Temperatura: ${"%.2f".format(temp)} °C" }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // logica de citire GPS
    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        lon = location.longitude
        alt = location.altitude
        viteza = location.speed
        eroare = location.accuracy
        satsUsed = location.extras?.getInt("satellites") ?: 0

        tvGpsLatLon.text = "Lat/Lon: ${"%.5f".format(lat)}, ${"%.5f".format(lon)}"
        tvGpsAltSpeed.text = "Altitudine/Viteză: ${"%.1f".format(alt)} m, ${"%.1f".format(viteza)} m/s"
        tvGpsErrSats.text = "Eroare/Sateliți: ${"%.1f".format(eroare)} m, $satsUsed folosiți"
    }

    // salvarea in ambele tabele
    private fun salveazaInBazaDeDate() {
        val timpCurent = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())

            // 1. cream obiectul pentru senzori hardware
            val inregTel =
                SenzoriTel(0, timpCurent, ax, ay, az, gx, gy, gz, mx, my, mz, ox, oy, oz, bar, temp)
            db.senzoriTelDao().insert(inregTel)

            // 2. cream obiectul pentru GPS
            val inregGps =
                SenzoriGps(0, timpCurent, lat, lon, alt, viteza, eroare, satsUsed, satsCount)
            db.senzoriGpsDao().insert(inregGps)

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Date hardware și GPS salvate!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}