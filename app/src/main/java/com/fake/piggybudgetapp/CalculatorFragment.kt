package com.fake.piggybudgetapp.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fake.piggybudgetapp.R
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorFragment : DialogFragment() {

    private lateinit var inputTextView: TextView
    private lateinit var resultTextView: TextView
    private var currentInput = ""

    private var calculatorResultListener: ((String) -> Unit)? = null

    fun setCalculatorResultListener(listener: (String) -> Unit) {
        calculatorResultListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        inputTextView = view.findViewById(R.id.inputTextView)
        resultTextView = view.findViewById(R.id.resultTextView)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide,
            R.id.btnClear, R.id.btnEquals, R.id.btnDone
        )

        for (id in buttons) {
            view.findViewById<Button>(id).setOnClickListener { handleButtonClick((it as Button).text.toString()) }
        }

        return view
    }

    private fun handleButtonClick(text: String) {
        when (text) {
            "=" -> evaluateExpression()
            "Clear" -> {
                currentInput = ""
                inputTextView.text = ""
                resultTextView.text = ""
            }
            "Done" -> {
                calculatorResultListener?.invoke(resultTextView.text.toString())
                dismiss()
            }
            else -> {
                currentInput += text
                inputTextView.text = currentInput
            }
        }
    }

    private fun evaluateExpression() {
        try {
            val expression = ExpressionBuilder(currentInput).build()
            val result = expression.evaluate()
            resultTextView.text = result.toString()
        } catch (e: Exception) {
            resultTextView.text = "Error"
        }
    }
    }

