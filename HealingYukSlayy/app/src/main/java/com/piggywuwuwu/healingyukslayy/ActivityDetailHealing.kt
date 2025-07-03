package com.piggywuwuwu.healingyukslayy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.piggywuwuwu.healingyukslayy.databinding.ActivityDetailHealingBinding
import org.json.JSONObject
import com.android.volley.Request
import com.squareup.picasso.Picasso

class ActivityDetailHealing : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHealingBinding
    private lateinit var location: HealingLocation
    private var isFavoriteView: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHealingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("HealingYukPrefs", Context.MODE_PRIVATE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Healing"

        location = intent.getParcelableExtra("EXTRA_LOCATION")
            ?: throw NullPointerException("HealingLocation is null!")
        isFavoriteView = intent.getBooleanExtra("EXTRA_IS_FAVORITE_VIEW", false)

        setupView()
        checkFavoriteStatus()
        setupFavoriteButton()
    }

    private fun setupView() {
        binding.tvDetailName.text = location.name
        binding.tvDetailCategory.text = location.category
        binding.tvDetailDescription.text = location.shortDescription
        binding.tvFullDescription.text = location.fullDescription

        Picasso.get()
            .load(location.photoUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(binding.ivHealingDetail)
    }

    private fun checkFavoriteStatus() {
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId == -1) return

        val params = JSONObject().apply {
            put("user_id", userId)
            put("location_id", location.id.toInt())
        }

        ApiUtils.makeApiCall(
            this,
            "favorites.php?user_id=$userId",
            Request.Method.GET,
            null,
            onSuccess = { response ->
                if (response.getBoolean("status")) {
                    val favorites = response.getJSONArray("data")
                    for (i in 0 until favorites.length()) {
                        val item = favorites.getJSONObject(i)
                        if (item.getString("id") == location.id) {
                            location.isFavorite = true
                            updateFavoriteButtonText()
                            break
                        }
                    }
                }
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupFavoriteButton() {
        updateFavoriteButtonText()

        binding.btnFavourite.setOnClickListener {
            val userId = sharedPreferences.getInt("user_id", -1)
            if (userId == -1) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val locationId = try {
                location.id.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid location ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (location.isFavorite) {
                removeFavorite(userId, locationId)
            } else {
                addFavorite(userId, locationId)
            }
        }
    }

    private fun addFavorite(userId: Int, locationId: Int) {
        val params = JSONObject().apply {
            put("user_id", userId)
            put("location_id", locationId)
        }

        ApiUtils.makeApiCall(
            this,
            "favorites.php",
            Request.Method.POST,
            params,
            onSuccess = { response ->
                handleFavoriteResponse(response)
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun removeFavorite(userId: Int, locationId: Int) {
        ApiUtils.makeApiCall(
            this,
            "favorites.php?user_id=$userId&location_id=$locationId",
            Request.Method.DELETE,
            null,
            onSuccess = { response ->
                handleFavoriteResponse(response)
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun handleFavoriteResponse(response: JSONObject) {
        if (response.getBoolean("status")) {
            location.isFavorite = !location.isFavorite
            updateFavoriteButtonText()
            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
            if (isFavoriteView && !location.isFavorite) {
                finish()
            }
        } else {
            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteButtonText() {
        binding.btnFavourite.text = if (location.isFavorite) {
            getString(R.string.remove_favourite)
        } else {
            getString(R.string.add_to_favourite)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}