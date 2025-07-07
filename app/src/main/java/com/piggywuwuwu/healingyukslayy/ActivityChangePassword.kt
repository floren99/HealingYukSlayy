package com.piggywuwuwu.healingyukslayy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piggywuwuwu.healingyukslayy.databinding.ActivityChangePasswordBinding
import org.json.JSONObject
import android.widget.Toast
import com.android.volley.Request

class ActivityChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("HealingYukPrefs", Context.MODE_PRIVATE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Change Password"

        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()
            val repeatPassword = binding.etRepeatNewPassword.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != repeatPassword) {
                Toast.makeText(this, "New passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sharedPreferences.getInt("user_id", -1)
            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val params = JSONObject().apply {
                put("user_id", userId)
                put("old_password", oldPassword)
                put("new_password", newPassword)
                put("repeat_password", repeatPassword)
            }

            ApiUtils.makeApiCall(
                this,
                "change_password.php",
                Request.Method.POST,
                params,
                onSuccess = { response ->
                    if (response.getBoolean("status")) {
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
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