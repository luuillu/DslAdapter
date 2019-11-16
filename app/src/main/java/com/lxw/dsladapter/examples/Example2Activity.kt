package com.lxw.dsladapter.examples

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import com.lxw.dsladapter.utils.DslAdapter
import com.lxw.dsladapter.utils.layoutParams

@SuppressLint("SetTextI18n")
class Example2Activity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_1)
        recyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)


        class Data(val name: String, val symbol: String, val price: Double, val ratio: Double)

        val dataList = listOf(
            Data("Tarena", "TEDU", 0.873, 5.17),
            Data("VipShor", "VIPS", 12.5, 3.73),
            Data("Momo", "MOMO", 38.34, 3.43),
            Data("Alibaba", "BABA", 185.49, 1.47)
        )

        recyclerView.adapter = DslAdapter(dataList).also {

            it<ViewGroup>(R.layout.list_item_example_2) {

                val nameView = findViewById<TextView>(R.id.name)
                val symbolView = findViewById<TextView>(R.id.symbol)
                val priceView = findViewById<TextView>(R.id.price)
                val ratioView = findViewById<TextView>(R.id.ratio)

                it.onBind {
                    nameView.text = it.name
                    symbolView.text = it.symbol
                    priceView.text = it.price.toString()
                    ratioView.text = it.ratio.toString() + "%"
                }

            }
        }
    }

}
