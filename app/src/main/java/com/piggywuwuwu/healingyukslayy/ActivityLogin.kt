package com.piggywuwuwu.healingyukslayy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piggywuwuwu.healingyukslayy.databinding.ActivityLoginBinding
import org.json.JSONObject
import android.content.SharedPreferences
import android.widget.Toast
import com.android.volley.Request

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("HealingYukPrefs", Context.MODE_PRIVATE)

        if (sharedPreferences.getInt("user_id", -1) != -1) {
            startMainActivity()
            return
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, ActivitySignUp::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val params = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            ApiUtils.makeApiCall(
                this,
                "login.php",
                Request.Method.POST,
                params,
                onSuccess = { response ->
                    if (response.getBoolean("status")) {
                        val user = response.getJSONObject("data")
                        saveUserSession(
                            user.getInt("id"),
                            user.getString("name"),
                            user.getString("email"),
                            user.getString("created_at")
                        )
                        startMainActivity()
                    } else {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun saveUserSession(userId: Int, name: String, email: String, joinedDate: String) {
        with(sharedPreferences.edit()) {
            putInt("user_id", userId)
            putString("user_name", name)
            putString("user_email", email)
            putString("user_joined_date", joinedDate)
            apply()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}