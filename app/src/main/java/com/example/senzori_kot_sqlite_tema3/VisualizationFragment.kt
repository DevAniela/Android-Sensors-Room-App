import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.senzori_kot_sqlite_tema3.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class VisualizationFragment : Fragment(), SensorEventListener {

    // declararea elementelor de interfata si a managerului de senzori
    private lateinit var lineChart: LineChart
    private lateinit var spinner: Spinner
    private lateinit var sensorManager: SensorManager

    // variabila care retine ce senzor am selectat din meniu (implicit accelerometrul)
    private var senzorCurentAles: Int = Sensor.TYPE_ACCELEROMETER

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_visualization, container, false)

        lineChart = view.findViewById(R.id.lineChart)
        spinner = view.findViewById(R.id.spinnerSelectieDate)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        configurareSpinner()
        configurareGrafic()

        return view
    }

    // setam meniul derulant cu optiunile de senzori
    private fun configurareSpinner() {
        val optiuni = arrayOf("Accelerometru", "Giroscop", "Magnetometru")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, optiuni)
        spinner.adapter = adapter

        // cand utilizatorul alege un senzor nou, curatam graficul si incepem sa desenam alte date
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                senzorCurentAles = when (position) {
                    0 -> Sensor.TYPE_ACCELEROMETER
                    1 -> Sensor.TYPE_GYROSCOPE
                    2 -> Sensor.TYPE_MAGNETIC_FIELD
                    else -> Sensor.TYPE_ACCELEROMETER
                }
                // curata graficul vechi la fiecare schimbare
                lineChart.clearValues()
                lineChart.data = LineData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // designul initial al graficului
    private fun configurareGrafic() {
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setDrawGridBackground(false)
        lineChart.data = LineData() // pornim cu date goale
    }

    // cream liniile (pensulele) pentru fiecare axa cu culori diferite
    private fun creazaSetDate(eticheta: String, culoare: Int): LineDataSet {
        val set = LineDataSet(null, eticheta)
        set.axisDependency = com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT
        set.color = culoare
        set.setDrawCircles(false) // ascundem punctele, vrem doar o linie continua
        set.lineWidth = 2f
        set.setDrawValues(false) // ascundem cifrele de pe grafic ca sa nu se aglomereze
        return set
    }

    // functia care adauga un punct nou pe grafic de fiecare data cand senzorul se misca
    private fun adaugaPunctPeGrafic(x: Float, y: Float, z: Float) {
        val data = lineChart.data ?: return

        var setX = data.getDataSetByIndex(0)
        var setY = data.getDataSetByIndex(1)
        var setZ = data.getDataSetByIndex(2)

        // daca liniile nu exista le cream acum
        if (setX == null) {
            setX = creazaSetDate("Axa X", Color.RED)
            data.addDataSet(setX)
            setY = creazaSetDate("Axa Y", Color.GREEN)
            data.addDataSet(setY)
            setZ = creazaSetDate("Axa Z", Color.BLUE)
            data.addDataSet(setZ)
        }

        // adaugam valorile la capatul liniilor
        data.addEntry(Entry(setX.entryCount.toFloat(), x), 0)
        data.addEntry(Entry(setY.entryCount.toFloat(), y), 1)
        data.addEntry(Entry(setZ.entryCount.toFloat(), z), 2)

        // anuntam graficul ca are date noi
        data.notifyDataChanged()
        lineChart.notifyDataSetChanged()

        // pastram pe ecran doar ultimele 50 de puncte ca sa para ca se misca
        lineChart.setVisibleXRangeMaximum(50f)
        lineChart.moveViewToX(data.entryCount.toFloat())
    }

    // pornim senzorii cand intram pe ecran
    override fun onResume() {
        super.onResume()
        val senzori = listOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD)
        for (tip in senzori) {
            sensorManager.getDefaultSensor(tip)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) // DELAY_UI e perfect pt grafice
            }
        }
    }

    // oprim senzorii cand iesim
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // functia chemata de android cand se misca telefonul
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // desenam pe grafic DOAR daca senzorul care s-a miscat este cel selectat in meniu
        if (event.sensor.type == senzorCurentAles) {
            adaugaPunctPeGrafic(event.values[0], event.values[1], event.values[2])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}