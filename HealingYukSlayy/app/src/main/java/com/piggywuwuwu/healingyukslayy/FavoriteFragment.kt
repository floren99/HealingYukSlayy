package com.piggywuwuwu.healingyukslayy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.piggywuwuwu.healingyukslayy.databinding.FragmentFavoriteBinding
import org.json.JSONArray
import org.json.JSONObject
import android.widget.Toast
import com.android.volley.Request
import com.squareup.picasso.Picasso

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteAdapter
    private val favorites = mutableListOf<HealingLocation>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(
            favorites,
            onItemClick = { location ->
                val detailIntent = Intent(requireContext(), ActivityDetailHealing::class.java)
                detailIntent.putExtra("EXTRA_LOCATION", location)
                detailIntent.putExtra("EXTRA_IS_FAVORITE_VIEW", true)
                startActivity(detailIntent)
            }
        )

        binding.rvFavourite.adapter = adapter
        binding.rvFavourite.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFavorites() {
        val sharedPreferences = requireContext().getSharedPreferences("HealingYukPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        ApiUtils.makeApiCall(
            requireContext(),
            "favorites.php?user_id=$userId",
            Request.Method.GET,
            null,
            onSuccess = { response ->
                if (response.getBoolean("status")) {
                    favorites.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        favorites.add(
                            HealingLocation(
                                id = item.getString("id"),
                                name = item.getString("name"),
                                category = item.getString("category_name"),
                                photoUrl = item.getString("photo_url"),
                                shortDescription = item.getString("short_description"),
                                fullDescription = item.getString("full_description"),
                                isFavorite = true
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            onError = { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}