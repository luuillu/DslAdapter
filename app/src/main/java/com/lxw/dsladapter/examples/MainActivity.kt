package com.lxw.dsladapter.examples

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.example_1).setOnClickListener {
            startActivity(Intent(this, Example1Activity::class.java))
        }

        findViewById<View>(R.id.example_2).setOnClickListener {
            startActivity(Intent(this, Example2Activity::class.java))
        }

        findViewById<View>(R.id.example_3).setOnClickListener {
            startActivity(Intent(this, Example3Activity::class.java))
        }

        findViewById<View>(R.id.example_4).setOnClickListener {
            startActivity(Intent(this, Example4Activity::class.java))
        }

        findViewById<View>(R.id.example_5).setOnClickListener {
            startActivity(Intent(this, Example5Activity::class.java))
        }
    }
}
