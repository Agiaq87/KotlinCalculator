package com.giaquinto.kalc

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private var firstDigit: Boolean = true
    private var firstStack: Boolean = true
    private var isInteger: Boolean = true

    private var accumulator: Double = 0.0
    private var lastOperation: String = ""

    private lateinit var resultTextView: TextView
    private lateinit var stackTextView: TextView

    private lateinit var handler: Handler
    private var executorService = Executors.newCachedThreadPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(Looper.getMainLooper())

        resultTextView = findViewById(R.id.textView)
        stackTextView = findViewById(R.id.textView2)
    }


    fun insertNum(view: View) {
        if (firstDigit && resultTextView.text.toString() == "0") {
            firstDigit = false
            resultTextView.text = view.getTag().toString()
        } else {
            resultTextView.text = resultTextView.text.toString() + view.getTag().toString()
        }
    }

    fun insertZero(view: View) {
        if (resultTextView.text.toString() != "0") {
            resultTextView.text = resultTextView.text.toString() + "0"
        }
    }

    fun delete(view: View) {
        firstDigit = true
        resultTextView.text = "0"
        handler.postDelayed(
            {
                stackTextView.text = getString(R.string.history)
            },
            500
        )
        stackTextView.text = "Delete all"

        executorService.execute {
            accumulator = 0.0
        }
    }

    fun decimal(view: View) {
        isInteger = false
        resultTextView.text = resultTextView.text.toString() + "."
    }

    fun pow(view: View) {
        val op = view.getTag().toString()
        var temp = resultTextView.text.toString().toDouble()
        var result: String = ""

        when(op) {
            "^2" ->  temp = temp.pow(2)
            "^3" ->  temp = temp.pow(3)
            "^4" ->  temp = temp.pow(4)
            "^5" ->  temp = temp.pow(5)
            "r2" ->  temp = temp.pow(0.5)
            "r3" ->  temp = Math.cbrt(temp)
            "r4" ->  temp = temp.pow(0.25)
            "r5" ->  temp = temp.pow(0.2)
            "ex" ->  temp = E.pow(temp)
            "10x" -> temp = 10.0.pow(temp)
            "ln" -> temp = log(temp, E)
            "log10" -> temp = log10(temp)
        }

        isInteger = temp.toString().contains(".0")

        result = temp.toString()

        if (isInteger) {
            result = result.subSequence(0, result.indexOf(".")).toString()
        }

        resultTextView.text = result

    }

    fun arithOp(view: View) {

        if (firstStack) {
            stackTextView.text = ""
            firstStack = false

            stackTextView.text = resultTextView.text.toString() + view.getTag().toString()

            accumulator = resultTextView.text.toString().toDouble()

            Log.e("ACCUMULATOR", accumulator.toString())

        } else {
            stackTextView.text =
                stackTextView.text.toString() +
                resultTextView.text.toString() + view.getTag().toString()

            val temp: Double = resultTextView.text.toString().toDouble()

            executorService.execute{
                modelOperation(temp)
            }
        }

        resultTextView.text = "0"
        firstDigit = true
        lastOperation = view.getTag().toString()
    }

    fun result(view: View) {

        if (stackTextView.text == getString(R.string.history)) {
            return
        }

        stackTextView.text = stackTextView.text.toString() + resultTextView.text.toString() + "="

        modelOperation(resultTextView.text.toString().toDouble())

        var temp: String = accumulator.toString()

        if (isInteger) {
            temp = temp.subSequence(0, temp.indexOf(".")).toString()
        }
        resultTextView.text = temp

        executorService.execute {
            firstDigit = true
            firstStack = true
            isInteger = true
        }
    }

    fun round(view: View) {
        when(view.getTag().toString()) {
            "int" -> resultTextView.text = resultTextView.text.toString().toDouble().roundToInt().toString()
            "trunc" -> resultTextView.text = truncate(resultTextView.text.toString().toDouble()).toString()
        }
    }

    fun pm(view: View) {
        resultTextView.text = "-" + resultTextView.text
        accumulator = - accumulator
        Log.e("PM", accumulator.toString())
    }

    fun fract(view: View) {
        resultTextView.text = (1/resultTextView.text.toString().toDouble()).toString()
        isInteger = false
    }


    private fun modelOperation(temp: Double) {
        Log.e("ModelOperation", lastOperation)
        when(lastOperation) {
            "+" -> {
                accumulator += temp
            }
            "-" -> {
                accumulator -= temp
            }
            "*" -> {
                accumulator *= temp
            }
            "/" -> {
                accumulator /= temp
            }
        }
        Log.e("EXECUTOR", accumulator.toString())
    }

    fun random(view: View) {
        when(view.getTag().toString()) {
            "int" -> resultTextView.text = ((Math.random()*100).roundToInt()).toString()
            "double" -> {
                resultTextView.text = Math.random().toString().subSequence(0,16)
                isInteger = false
            }
        }
    }
}