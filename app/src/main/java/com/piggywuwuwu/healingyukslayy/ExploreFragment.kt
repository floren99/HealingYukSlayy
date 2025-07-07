package com.piggywuwuwu.healingyukslayy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.piggywuwuwu.healingyukslayy.databinding.FragmentExploreBinding
import org.json.JSONArray
import org.json.JSONObject
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.squareup.picasso.Picasso

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HealingAdapter
    private val locations = mutableListOf<HealingLocation>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadLocations()

        binding.fabAddLocation.setOnClickListener {
            val intent = Intent(requireContext(), ActivityNewLocation::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = HealingAdapter(
            locations,
            onItemClick = { location ->
            },
            onReadMoreClick = { location ->
                val detailIntent = Intent(requireContext(), ActivityDetailHealing::class.java)
                detailIntent.putExtra("EXTRA_LOCATION", location)
                startActivity(detailIntent)
            }
        )

        binding.rvHealing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHealing.adapter = adapter
    }

    private fun loadLocations() {
        ApiUtils.makeApiCall(
            requireContext(),
            "locations.php",
            Request.Method.GET,
            null,
            onSuccess = { response ->
                if (response.getBoolean("status")) {
                    locations.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        locations.add(
                            HealingLocation(
                                id = item.getString("id"),
                                name = item.getString("name"),
                                category = item.getString("category_name"),
                                photoUrl = item.getString("photo_url"),
                                shortDescription = item.getString("short_description"),
                                fullDescription = item.getString("full_description")
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

    private val locationAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadLocations()
        }
    }

    override fun onResume() {
        super.onResume()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(
                locationAddedReceiver,
                IntentFilter("LOCATION_ADDED"),
                Context.RECEIVER_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                requireActivity(),
                locationAddedReceiver,
                IntentFilter("LOCATION_ADDED"),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
        loadLocations()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(locationAddedReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ExploreFragment()
    }
}