package com.piggywuwuwu.healingyukslayy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piggywuwuwu.healingyukslayy.databinding.ItemHealingCardBinding
import com.squareup.picasso.Picasso

class HealingAdapter(
    private val locations: List<HealingLocation>,
    private val onItemClick: (HealingLocation) -> Unit,
    private val onReadMoreClick: (HealingLocation) -> Unit
) : RecyclerView.Adapter<HealingAdapter.HealingViewHolder>() {

    inner class HealingViewHolder(private val binding: ItemHealingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: HealingLocation) {
            binding.tvHealingName.text = location.name
            binding.tvHealingCategory.text = location.category
            binding.tvHealingDescription.text = location.shortDescription

            Picasso.get()
                .load(location.photoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(binding.ivHealingImage)

            binding.btnReadMore.setOnClickListener {
                onReadMoreClick(location)
            }

            binding.root.setOnClickListener {
                onItemClick(location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealingViewHolder {
        val binding = ItemHealingCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HealingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HealingViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int = locations.size
}