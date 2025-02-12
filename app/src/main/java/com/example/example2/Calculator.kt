package com.example.example2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.media.SoundPool
import android.media.AudioAttributes
import java.util.Stack

class Calculator : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private lateinit var txtResult: TextView
    private var soundId: Int = 0
    private var input: String = ""
    private var result: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        txtResult = findViewById<TextView>(R.id.txtResult)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool.load(this, R.raw.click, 1)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btnPlus, R.id.btnMinus,
            R.id.btnMultiply, R.id.btnDivide, R.id.btnClear, R.id.btnEquals
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
                onButtonClick(it as Button)
            }
        }
    }

    private fun onButtonClick(button: Button) {
        when (button.text) {
            "C" -> {
                input = ""
                result = ""
            }
            "=" -> {
                result = calculateResult(input)
                input = result
            }
            else -> {
                input += button.text
            }
        }
        txtResult.text = input
    }

    private fun calculateResult(input: String): String {
        return try {
            evaluateExpression(input).toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val ops = Stack<Char>()
        val values = Stack<Double>()

        val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2)

        fun applyOp(op: Char, b: Double, a: Double): Double {
            return when (op) {
                '+' -> a + b
                '-' -> a - b
                '*' -> a * b
                '/' -> a / b
                else -> throw IllegalArgumentException("Operator tidak dikenal: $op")
            }
        }

        val tokens = expression.replace("x", "*").toCharArray()
        var i = 0

        while (i < tokens.size) {
            when {
                tokens[i].isDigit() -> {
                    var num = ""
                    while (i < tokens.size && (tokens[i].isDigit() || tokens[i] == '.')) {
                        num += tokens[i]
                        i++
                    }
                    values.push(num.toDouble())
                    i--
                }
                tokens[i] in precedence -> {
                    while (ops.isNotEmpty() && precedence.getValue(ops.peek()) >= precedence.getValue(tokens[i])) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    }
                    ops.push(tokens[i])
                }
            }
            i++
        }

        while (ops.isNotEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }
}