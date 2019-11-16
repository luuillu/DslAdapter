package com.lxw.dsladapter.examples

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.lxw.dsladapter.utils.DslMultiTypeAdapter
import com.lxw.dsladapter.utils.DslPlaceHolder

@SuppressLint("SetTextI18n")
class Example5Activity : AppCompatActivity(), View.OnClickListener {

    private lateinit var recyclerView: RecyclerView

    private lateinit var button1: Button
    private lateinit var button2: Button

    private var isReceivedData1 = false
    private var isReceivedData2 = false

    private var data1 = DslPlaceHolder<Int>(null)
    private var data2 = DslPlaceHolder<Double>(null)

    val adapter = getListAdapter(listOf(data1, data2))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_example_5)
        button1 = findViewById<Button>(R.id.btn_1)
        button2 = findViewById<Button>(R.id.btn_2)

        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        recyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private  fun getListAdapter(list:List<Any>) = DslMultiTypeAdapter(list).also {

        it<Int, TextView> {
            it.onBind {
                text = "Received data1 value:$it"
            }
        }

        it<Double?, TextView> {
            it.onBind {

                text = if (it != null) {
                    "Received data2 value:$it"
                } else {
                    "Data2 is not available"
                }

            }
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_1 -> {
                isReceivedData1 = !isReceivedData1
            }
            R.id.btn_2 -> {
                isReceivedData2 = !isReceivedData2
            }
        }
        update()
    }

    @SuppressLint("SetTextI18n")
    fun update() {
        if (isReceivedData1) {
            button1.setText("Remove Data1")

            data1.value = 100
            data1.notify(adapter)

        } else {
            button1.setText("Receive Data1")

            data1.value = null
            data1.notify(adapter)
        }

        if (isReceivedData2) {
            button2.setText("Remove Data2")

            data2.value = 2000.0
            data2.notify(adapter)
        } else {
            button2.setText("Receive Data2")

            data2.value = null
            data2.notify(adapter)
        }
    }

}
