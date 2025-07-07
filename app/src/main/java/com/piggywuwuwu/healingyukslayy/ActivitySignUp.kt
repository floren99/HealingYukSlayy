package com.piggywuwuwu.healingyukslayy
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piggywuwuwu.healingyukslayy.databinding.ActivitySignUpBinding
import org.json.JSONObject
import android.widget.Toast
import com.android.volley.Request

class ActivitySignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign Up"

        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val repeatPassword = binding.etRepeatPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val params = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", password)
                put("repeat_password", repeatPassword)
            }

            ApiUtils.makeApiCall(
                this,
                "signup.php",
                Request.Method.POST,
                params,
                onSuccess = { response ->
                    if (response.getBoolean("status")) {
                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ActivityLogin::class.java)
                        startActivity(intent)
                        finish()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}