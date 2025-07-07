package com.piggywuwuwu.healingyukslayy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piggywuwuwu.healingyukslayy.databinding.ItemFavoriteCardBinding
import com.squareup.picasso.Picasso

class FavoriteAdapter(
    private val favorites: List<HealingLocation>,
    private val onItemClick: (HealingLocation) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(private val binding: ItemFavoriteCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: HealingLocation) {
            binding.tvFavoriteName.text = location.name
            binding.tvFavoriteCategory.text = location.category

            Picasso.get()
                .load(location.photoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(binding.ivFavoriteImage)

            binding.root.setOnClickListener {
                onItemClick(location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size
}