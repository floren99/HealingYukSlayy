package com.piggywuwuwu.healingyukslayy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.piggywuwuwu.healingyukslayy.databinding.FragmentProfileBinding
import org.json.JSONObject
import android.widget.Toast
import com.android.volley.Request
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("HealingYukPrefs", Context.MODE_PRIVATE)
        loadProfileData()
    }

    private fun loadProfileData() {
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId == -1) {
            binding.tvName.text = "Guest"
            binding.tvEmail.text = "Not logged in"
            binding.tvJoinedDate.text = "-"
            binding.tvTotalFavourites.text = "0"
            return
        }

        binding.tvName.text = sharedPreferences.getString("user_name", "")
        binding.tvEmail.text = sharedPreferences.getString("user_email", "")
        binding.tvJoinedDate.text = sharedPreferences.getString("user_joined_date", "")

        ApiUtils.makeApiCall(
            requireContext(),
            "profile.php?user_id=$userId",
            Request.Method.GET,
            null,
            onSuccess = { response ->
                if (response.getBoolean("status")) {
                    val data = response.getJSONObject("data")
                    binding.tvTotalFavourites.text = data.getString("favorite_count")
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
        loadProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}