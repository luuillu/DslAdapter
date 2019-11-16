package com.lxw.dsladapter.examples

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import com.lxw.dsladapter.utils.DslAdapter
import com.lxw.dsladapter.utils.DslDefaultInflater
import com.lxw.dsladapter.utils.dip
import com.lxw.dsladapter.utils.layoutParams


class Example3Activity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_example_3)
        recyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val scoreList = listOf("Jim Green" to 80, "LiLei" to 90, "HanMeiMei" to 95, "Polly" to 100)

        recyclerView.adapter = getAdapter(scoreList)

    }

}

private fun getListStyle(otherStyle: TextView.() -> Unit) =
    arrayOf(1f, 1f).mapIndexed { index, weight ->

        //此处把参数otherStyle定义成局部变量没有实际的用处，只为了避免编译器Bug
        val otherSt = otherStyle

        object {
            val lParam = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                this.weight = weight
            }

            val gravity = if (index == 0) {
                Gravity.START
            } else {
                Gravity.END
            }


            fun applyStyle(textView: TextView) {
                textView.layoutParams = lParam
                textView.gravity = gravity


                textView.otherSt()
            }
        }
    }.iterator()

fun getAdapter(list: List<Pair<String, Int>>) = DslAdapter(list).also {

    it<LinearLayout> {
        it.layoutParams {
            width = MATCH_PARENT
        }
        orientation = HORIZONTAL

        val styleIterator = getListStyle {
            setTextColor(Color.parseColor("black"))
        }

        it<TextView> {
            styleIterator.next().applyStyle(this)

            it.onBind {
                text = it.first
            }
        }

        it<TextView> {
            styleIterator.next().applyStyle(this)

            it.onBind {
                text = it.second.toString()
            }
        }
    }
}

@SuppressLint("SetTextI18n")
class ListHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL

        val styleIterator = getListStyle {
            setTextColor(Color.parseColor("gray"))
        }


        DslDefaultInflater(this).let {

            it.layoutParams {
                topMargin =  dip(4)
                bottomMargin = dip(4)
            }

            it<TextView> {
                styleIterator.next().applyStyle(this)
                text = "name"
            }

            it<TextView> {
                styleIterator.next().applyStyle(this)
                text = "score"
            }
        }
    }


}
