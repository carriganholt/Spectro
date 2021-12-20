package com.example.spectro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLoadPicture: Button
    private lateinit var buttonCalculate: Button
    private lateinit var textBox: TextView
    private lateinit var image: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Spectrophotometer"

        buttonLoadPicture = findViewById(R.id.buttonLoadPicture)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textBox = findViewById(R.id.textView)
        image = findViewById(R.id.imageView2)
        val toList = findViewById<Button>(R.id.toList)
        val graph = findViewById<View>(R.id.graph) as GraphView

        image.setImageResource(R.drawable.colors)

        graph.viewport.setMinX(400.0)
        graph.viewport.setMaxX(700.0)
        graph.viewport.setMinY(0.0)
        graph.gridLabelRenderer.isVerticalLabelsVisible = false

        graph.viewport.isXAxisBoundsManual = true

        var interval: Int
        var array: DoubleArray? = null

        buttonLoadPicture.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        buttonCalculate.setOnClickListener {

            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()

            if (imageUri != null) {

                val pixels = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                var rowRed: Double
                var rowGreen: Double
                var rowBlue: Double
                var x: Int
                var y = 0
                array = DoubleArray(300)
                interval = (pixels.height / 300.0).toInt()
                val pointList = ArrayList<DataPoint>()

                var counter = 0
                while (y < pixels.height && counter < 300) {
                    x = 0
                    rowRed = 0.0
                    rowGreen = 0.0
                    rowBlue = 0.0

                    while (x < pixels.width) {
                        val pixel = pixels.getPixel(x, y)
                        rowRed += Color.red(pixel)
                        rowGreen += Color.green(pixel)
                        rowBlue += Color.blue(pixel)
                        x++
                    }
                    array!![counter] = rowRed + rowGreen + rowBlue
                    counter++
                    y += interval
                }

                try {
                for (i in (0..299)) {
                    pointList.add(DataPoint((400.0 + i), array!![i]))
                }
                } catch(e: Exception) {
                    textBox.text = e.toString()
                }

                val series = LineGraphSeries(pointList.toTypedArray())
                graph.removeAllSeries()
                series.color = Color.BLACK
                graph.addSeries(series)
            }
        }

        graph.setOnTouchListener(OnTouchListener { view, event ->
            if(imageUri != null) {
                textBox.text = "" + array!![(event.x.toDouble() * 300.0 / graph.width.toDouble()).toInt()].toInt()
            }
            true
        })

        toList.setOnClickListener {
            if (imageUri != null) {
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra("graph", array)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            buttonCalculate.setBackgroundColor(Color.GREEN)
        }
    }
}