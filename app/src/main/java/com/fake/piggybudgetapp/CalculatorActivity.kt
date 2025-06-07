package com.fake.piggybudgetapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculatorActivity : AppCompatActivity() {

    private lateinit var tvInput: TextView
    private var currentInput: String = ""
    private var lastOperator: String? = null
    private var result: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        tvInput = findViewById(R.id.tvInput)

        val buttonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        buttonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val digit = (it as Button).text.toString()
                currentInput += digit
                tvInput.text = currentInput
            }
        }

        findViewById<Button>(R.id.btnAdd).setOnClickListener { handleOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { handleOperator("-") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnDone).setOnClickListener { finish() }
    }

    private fun handleOperator(operator: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDouble()
            if (result == null) {
                result = value
            } else {
                result = performOperation(result!!, value, lastOperator)
            }
            lastOperator = operator
            currentInput = ""
            tvInput.text = "$result $operator"
        }
    }

    private fun calculateResult() {
        if (currentInput.isNotEmpty() && lastOperator != null) {
            val value = currentInput.toDouble()
            result = performOperation(result!!, value, lastOperator)
            tvInput.text = result.toString()
            currentInput = ""
            lastOperator = null
        }
    }

    private fun performOperation(a: Double, b: Double, operator: String?): Double {
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            else -> b
        }
    }
}