package com.example.spectro

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val list = findViewById<ListView>(R.id.list)
        val back = findViewById<Button>(R.id.back)
        val elements = ArrayList<String>()

        val extras = intent.extras
        val graph = extras!!.getDoubleArray("graph")
        var x = 1
        for (i in graph!!) {
            elements.add("" + x + ", " + i.toInt())
            x++
        }

        list.adapter = MyAdapter(elements, this)

        back.setOnClickListener {
            finish()
        }
    }

    class MyAdapter(private var data: ArrayList<String>, var context: Context) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val tv = TextView(context)
            tv.text = data[position]
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
            tv.setPadding(15, 15, 15, 15)
            return tv
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(position: Int): Any {
            return 0
        }

        override fun getItemId(position: Int): Long {
            return 0
        }
    }
}