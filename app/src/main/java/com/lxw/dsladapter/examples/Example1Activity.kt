package com.lxw.dsladapter.examples

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import com.lxw.dsladapter.utils.DslAdapter
import com.lxw.dsladapter.utils.layoutParams


class Example1Activity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_example_1)
        recyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val scoreList = listOf("Jim Green" to 80, "LiLei" to 90, "HanMeiMei" to 95, "Polly" to 100)


        recyclerView.adapter = DslAdapter(scoreList).also {

            /*
           * 创建ViewHolder对应的布局， 泛型反射表示要需要创建的View的类型
           * 后面紧跟的lambda表达式是一个扩展函数， receive type是泛型参数代表的View类型。
           * 所以可以在lambda表达式中对创建出来的View做一些初始化操作。
           */
            it<LinearLayout> {

                /*
                * 此处的it代表DslBindableInflater对象，它是一个辅助创建布局的工具类。
                * 主要有三个功能:
                *
                * 1.it<ViewType>{} ： 这个方法建一个childView，然后执行后面的lambda表达式初始化child，
                * 最后把child添加到自己的view中。
                *
                * 2.调用it.layoutParams{} 可以方便设置layoutParams,每个ViewGroup对应不同类型的LayoutParam，
                * 调用这个方法可以根据上下文关系，自动推导出正确的LayoutParam类型。
                *
                * 3.it.onBind{} 这个方法在Adapter执行onBind 的时候被调用，
                * 可以这个方法中得到这个item关联的数据，然后把数据展示出来
                */
                it.layoutParams {
                    width = MATCH_PARENT
                }

                orientation = HORIZONTAL

                //创建一个TextView对象，并添加到LinearLayout中
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

                    it.onBind {
                        text = it.second.toString()
                    }
                }
            }

        }
    }

}
