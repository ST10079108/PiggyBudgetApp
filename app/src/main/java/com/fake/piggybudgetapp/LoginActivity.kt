package com.fake.piggybudgetapp

import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fake.piggybudgetapp.database.FirebaseDbHelper
import com.fake.piggybudgetapp.database.UserEntity
import com.fake.piggybudgetapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() , View.OnClickListener{

    private lateinit var binding: ActivityLoginBinding

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

        // View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val username = binding.edtUsername.text.toString()
        val password = binding.edtPassword.text.toString()
        when (v?.id) {
            R.id.btnLogin -> getUserFromDB(username, password)
        }
    }

    // Fetch user from DB, show Toast, open next activity
    private fun getUserFromDB(username: String, password: String) {
        FirebaseDbHelper.getAllUsers { userList ->
            val user = userList.firstOrNull { it.username == username && it.password == password }

            user?.let {
                // Save to SharedPreferences as JSON
                JsonUtils.saveUserToPreferences(this@LoginActivity, it)

                // Go to next screen
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Welcome ${it.username}", Toast.LENGTH_SHORT).show()
                    openIntent(this@LoginActivity, it.username, HomeActivity::class.java)
                }
            }
        }
    }

}
