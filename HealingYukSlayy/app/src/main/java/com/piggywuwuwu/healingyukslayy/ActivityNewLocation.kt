package com.piggywuwuwu.healingyukslayy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piggywuwuwu.healingyukslayy.databinding.ActivityNewLocationBinding
import org.json.JSONObject
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request

class ActivityNewLocation : AppCompatActivity() {
    private lateinit var binding: ActivityNewLocationBinding
    private val categories = mutableListOf<Category>()

    data class Category(val id: Int, val name: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "New Location"

        loadCategories()

        binding.btnAddLocation.setOnClickListener {
            val name = binding.etLocationName.text.toString().trim()
            val selectedPosition = binding.spinnerCategory.selectedItemPosition
            val photoUrl = binding.etPhotoUrl.text.toString().trim()
            val shortDesc = binding.etShortDescription.text.toString().trim()
            val fullDesc = binding.etFullDescription.text.toString().trim()

            if (name.isEmpty() || photoUrl.isEmpty() || shortDesc.isEmpty() || fullDesc.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPosition < 0 || selectedPosition >= categories.size) {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryId = categories[selectedPosition].id

            val params = JSONObject().apply {
                put("name", name)
                put("category_id", categoryId)
                put("photo_url", photoUrl)
                put("short_description", shortDesc)
                put("full_description", fullDesc)
            }

            ApiUtils.makeApiCall(
                this,
                "locations.php",
                Request.Method.POST,
                params,
                onSuccess = { response ->
                    if (response.getBoolean("status")) {
                        Toast.makeText(this, "Location added successfully", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadCategories() {
        ApiUtils.makeApiCall(
            this,
            "categories.php",
            Request.Method.GET,
            null,
            onSuccess = { response ->
                if (response.getBoolean("status")) {
                    categories.clear()
                    val data = response.getJSONArray("data")
                    val categoryNames = mutableListOf<String>()

                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        categories.add(
                            Category(
                                id = item.getInt("id"),
                                name = item.getString("name")
                            )
                        )
                        categoryNames.add(item.getString("name"))
                    }

                    binding.spinnerCategory.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            onError = { error ->
                Toast.makeText(this, "Failed to load categories: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}