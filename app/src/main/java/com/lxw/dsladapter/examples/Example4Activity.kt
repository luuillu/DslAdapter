package com.lxw.dsladapter.examples

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import com.lxw.dsladapter.utils.DslAdapter
import com.lxw.dsladapter.utils.DslMultiTypeAdapter
import com.lxw.dsladapter.utils.layoutParams


class Example4Activity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_example_1)
        recyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)


        class HeaderData

        val adapter = DslMultiTypeAdapter().also {

            it<HeaderData, ListHeader> {
                it.layoutParams {
                    width = MATCH_PARENT
                }
            }

            it<Pair<String, Int>, LinearLayout> {

                it.layoutParams {
                    width = MATCH_PARENT
                }

                orientation = HORIZONTAL

                it<TextView> {

                    it.layoutParams {
                        width = 0
                        weight = 1f
                    }

                    it.onBind {
                        text = it.first
                    }
                }

                it<TextView> {

                    it.layoutParams {
                        width = 0
                        weight = 1f
                    }
                    gravity = Gravity.END

                    it.onBind {
                        text = it.second.toString()
                    }
                }
            }

        }

        adapter.add(HeaderData())
        val scoreList = listOf("Jim Green" to 80, "LiLei" to 90, "HanMeiMei" to 95, "Polly" to 100)
        adapter.addAll(scoreList)

        recyclerView.adapter = adapter
    }

}
