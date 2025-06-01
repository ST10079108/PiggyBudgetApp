package com.fake.piggybudgetapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.piggybudgetapp.database.CategoryTotal
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.databinding.ActivityStatsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private lateinit var categoryTotalsAdapter: CategoryTotalsAdapter
    private lateinit var barChart: BarChart
    private lateinit var fDate: String
    private lateinit var tDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryTotalsAdapter = CategoryTotalsAdapter(emptyList())
        binding.recyclerViewCategoryTotals.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCategoryTotals.adapter = categoryTotalsAdapter

        barChart = binding.BarChart

        loadTransactions()

        // Hide search form initially
        binding.llMember1.visibility = View.GONE
        binding.llMember2.visibility = View.GONE
        binding.llMember3.visibility = View.GONE

        // Button Actions
        binding.btnDisplaySearchForm.setOnClickListener {
            binding.llMember1.visibility = View.VISIBLE
            binding.llMember2.visibility = View.VISIBLE
            binding.llMember3.visibility = View.VISIBLE
        }

        binding.btnDisplayAll.setOnClickListener {
            binding.llMember1.visibility = View.GONE
            binding.llMember2.visibility = View.GONE
            binding.llMember3.visibility = View.GONE
            loadTransactions()
            binding.txtTransacions.text = "Category Totals"
        }

        binding.btnSearch.setOnClickListener {
            fDate = binding.etDateFrom.text.toString()
            tDate = binding.etDateTo.text.toString()
            if (fDate.isEmpty() || tDate.isEmpty()) {
                Toast.makeText(this, "Enter all details!", Toast.LENGTH_SHORT).show()
            } else {
                loadSearchedTransactions(fDate, tDate)
                binding.txtTransacions.text = "Category Totals between $fDate and $tDate"
            }
        }
    }

    private fun loadTransactions() {
        FirebaseDbHelper.getTransactionCategoryTotals { categoryTotals ->
            val completedTransactions = categoryTotals.filter {
                it.total != 0.0 && it.name.isNotBlank()
            }
            runOnUiThread {
                categoryTotalsAdapter.updateCategoryTotals(completedTransactions)
                updateBarChart(completedTransactions)
            }
        }
    }

    private fun loadSearchedTransactions(fD: String, tD: String) {
        FirebaseDbHelper.getTransactionCategoryTotalsBetweenDates(fD, tD) { categoryTotals ->
            val completedTransactions = categoryTotals.filter {
                it.total != 0.0 && it.name.isNotBlank()
            }
            runOnUiThread {
                categoryTotalsAdapter.updateCategoryTotals(completedTransactions)
                updateBarChart(completedTransactions)
            }
        }
    }

    private fun updateBarChart(categoryTotals: List<CategoryTotal>) {
        val entries = ArrayList<BarEntry>()
        categoryTotals.forEachIndexed { index, totalEntity ->
            entries.add(BarEntry(index.toFloat(), totalEntity.total.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "Category Totals")
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16f
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())

        val barData = BarData(barDataSet)
        barChart.data = barData
        barData.barWidth = 0.25f
        barChart.description.isEnabled = false

        FirebaseDbHelper.getAllGoals { goals ->
            runOnUiThread {
                if (goals.isNotEmpty()) {
                    val minLine = LimitLine(goals.last().minimumAmount.toFloat(), "Min Goal").apply {
                        lineWidth = 2f
                        textSize = 12f
                        lineColor = Color.RED
                        textColor = Color.RED
                    }

                    val maxLine = LimitLine(goals.last().maximumAmount.toFloat(), "Max Goal").apply {
                        lineWidth = 2f
                        textSize = 12f
                        lineColor = Color.GREEN
                        textColor = Color.GREEN
                    }

                    barChart.axisLeft.removeAllLimitLines()
                    barChart.axisLeft.addLimitLine(minLine)
                    barChart.axisLeft.addLimitLine(maxLine)
                    barChart.invalidate()
                }
            }
        }
        barChart.invalidate()
    }
}