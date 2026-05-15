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
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.senzori_kot_sqlite_tema3.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class VisualizationFragment : Fragment(), SensorEventListener {

    // declararea elementelor de interfata si a managerului de senzori
    private lateinit var lineChart: LineChart
    private lateinit var sensorManager: SensorManager

    // checkbox-urile de pe ecran
    private lateinit var chkAx: CheckBox
    private lateinit var chkAy: CheckBox
    private lateinit var chkAz: CheckBox
    private lateinit var chkGx: CheckBox

    // variabile pentru a tine evidenta timpului (axa X a graficului)
    private var timpCurent = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_visualization, container, false)

        lineChart = view.findViewById(R.id.lineChart)
        chkAx = view.findViewById(R.id.chkAx)
        chkAy = view.findViewById(R.id.chkAy)
        chkAz = view.findViewById(R.id.chkAz)
        chkGx = view.findViewById(R.id.chkGx)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        configurareGrafic()

        // cand debifam/bifam ceva, stergem graficul vechi ca sa o ia de la capat curat
        val listenerBife = View.OnClickListener { curataGraficul() }
        chkAx.setOnClickListener(listenerBife)
        chkAy.setOnClickListener(listenerBife)
        chkAz.setOnClickListener(listenerBife)
        chkGx.setOnClickListener(listenerBife)

        return view
    }

    // designul initial al graficului
    private fun configurareGrafic() {
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setDrawGridBackground(false)
        lineChart.data = LineData()
    }

    // curata graficul vechi la fiecare schimbare
    private fun curataGraficul() {
        lineChart.clearValues()
        lineChart.data = LineData()
        timpCurent = 0f
    }

    // cream liniile (pensulele) pentru fiecare axa cu culori diferite
    private fun creazaSetDate(eticheta: String, culoare: Int): LineDataSet {
        val set = LineDataSet(null, eticheta)
        set.axisDependency = com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT
        set.color = culoare
        set.setDrawCircles(false)
        set.lineWidth = 2f
        set.setDrawValues(false)
        return set
    }

    // primim valoarea, culoarea si numele, si o punem pe grafic
    private fun adaugaPunctPeGrafic(valoare: Float, eticheta: String, culoare: Int, indexLinie: Int) {
        val data = lineChart.data ?: return

        var set = data.getDataSetByIndex(indexLinie)
        if (set == null) {
            set = creazaSetDate(eticheta, culoare)
            data.addDataSet(set)
        }

        // anuntam graficul ca are date noi
        data.addEntry(Entry(timpCurent, valoare), indexLinie)
        data.notifyDataChanged()
        lineChart.notifyDataSetChanged()

        // pastram pe ecran doar ultimele 50 de puncte ca sa para ca se misca
        lineChart.setVisibleXRangeMaximum(50f)
        lineChart.moveViewToX(timpCurent)
    }

    // pornim senzorii cand intram pe ecran
    override fun onResume() {
        super.onResume()
        // pornim ambii senzori simultan
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // oprim senzorii cand iesim
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // functia apelata de android cand se misca telefonul
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        timpCurent += 0.1f // crestem usor timpul ca sa se miste graficul la dreapta

        // daca e accelerometru si bifele sunt puse, desenam
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            if (chkAx.isChecked) adaugaPunctPeGrafic(event.values[0], "AX", Color.RED, 0)
            if (chkAy.isChecked) adaugaPunctPeGrafic(event.values[1], "AY", Color.parseColor("#388E3C"), 1) // verde inchis
            if (chkAz.isChecked) adaugaPunctPeGrafic(event.values[2], "AZ", Color.BLUE, 2)
        }

        // daca e giroscop si bifa GX e pusa, desenam cu portocaliu
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            if (chkGx.isChecked) adaugaPunctPeGrafic(event.values[0], "GX", Color.parseColor("#F57C00"), 3) // portocaliu
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}