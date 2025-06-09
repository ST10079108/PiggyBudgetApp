package com.fake.piggybudgetapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fake.piggybudgetapp.database.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fake.piggybudgetapp.calculator.CalculatorFragment
import com.fake.piggybudgetapp.databinding.ActivityTransactionBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityTransactionBinding
    private lateinit var catResultLauncher: ActivityResultLauncher<Intent>
    private var imageByteArray: ByteArray? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            imageByteArray = stream.toByteArray()

            binding.ivSelectedImage.setImageBitmap(bitmap)
        }
    }



    private var transaction = TransactionEntity( "", 0.0, "", "", null, "", "")
    private var types = arrayOf(
        "Income", "Expense"
    )
    private var recurrences = arrayOf(
        "Annually", "Monthly", "Weekly", "Never"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide phone's ui
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Stop bottomnavigationbar from auto adding padding on startup
        val navBar = findViewById<BottomNavigationView>(R.id.navbar)
        navBar.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, 0)
            insets
        }

        navBar.selectedItemId = R.id.nav_transactions

        binding.navbar.setOnItemSelectedListener {
                item -> when (item.itemId) {
            R.id.nav_stats -> {
                val intent = Intent(this, StatsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_transactions -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_home -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_budget -> {
                val intent = Intent(this, BudgetHistory::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
        }

        catResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val categoryName = result.data?.getStringExtra("selectedCategoryName")
                binding.etCategory.setText(categoryName)
            }
        }

        // types spinner
        val spin = findViewById<Spinner>(R.id.spType)
        spin.onItemSelectedListener = this
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(this,
            android.R.layout.simple_spinner_item, types
        )
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spin.adapter = ad
        // recurrences spinner
        val recSpin = findViewById<Spinner>(R.id.spRecurrence)
        recSpin.onItemSelectedListener = this
        val ada: ArrayAdapter<*> = ArrayAdapter<Any?>(this,
            android.R.layout.simple_spinner_item, recurrences
        )
        ada.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        recSpin.adapter = ada

        binding.btnAddTransaction.setOnClickListener {
            //Get user input
            val tType = binding.spType.selectedItem.toString().trim()
            val tAmount = binding.etTransactionAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
            val tDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val tCategory = binding.etCategory.text.toString().trim()
            val tDescription = binding.etDescription.text.toString().trim()
            val tFrequency = binding.spRecurrence.selectedItem.toString().trim()

            if (tType.isEmpty() || tAmount == 0.0 || tDate.isEmpty() || tCategory.isEmpty() || tDescription.isEmpty() ||tFrequency.isEmpty() ) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //Update the transaction object
            transaction.type = tType
            transaction.amount = tAmount
            transaction.date = tDate
            transaction.category = tCategory
            transaction.description = tDescription
            transaction.recurrence = tFrequency

            // Save to SharedPreferences
            JsonUtils.saveTransactionToPreferences(this@TransactionActivity, transaction)

            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                FirebaseDbHelper.insertTransaction(transaction, imageByteArray) {
                    runOnUiThread {
                        Toast.makeText(this@TransactionActivity, "Transaction saved to history!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val intent = Intent(this, TransactionHistory::class.java)
            startActivity(intent)

            // reset input fields
            binding.etTransactionAmount.setText("")
            binding.etCategory.setText("")
            binding.etDescription.setText("")
        }

//        binding.btnSelectCategory.setOnClickListener {
//            val intent = Intent(this, CategoryHistory::class.java)
//            catResultLauncher.launch(intent)
//        }
        binding.etCategory.setOnClickListener {
            val intent = Intent(this, CategoryHistory::class.java)
            catResultLauncher.launch(intent)
        }
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.etTransactionAmount.setOnClickListener {
            val calculatorFragment = CalculatorFragment()
            calculatorFragment.setCalculatorResultListener { result ->
                binding.etTransactionAmount.setText(result)
            }
            calculatorFragment.show(supportFragmentManager, "calculator")

        }
        }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spType -> Toast.makeText(this, types[position], Toast.LENGTH_SHORT).show()
            R.id.spRecurrence -> Toast.makeText(this, recurrences[position], Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}


}
