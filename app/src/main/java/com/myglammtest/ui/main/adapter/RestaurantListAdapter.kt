package com.myglammtest.ui.main.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.myglammtest.R
import com.myglammtest.models.response.Restaurant
import com.myglammtest.models.response.RestaurantsResponse
import kotlinx.android.synthetic.main.layout_restaurant_item.view.*
import javax.inject.Inject

class RestaurantListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Restaurant>() {

        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.restaurant?.id == newItem.restaurant?.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return RestaurantViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_restaurant_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Restaurant>) {
        differ.submitList(list)
    }

    class RestaurantViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Restaurant) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            itemView.name_restaurant_textview.text = item.restaurant.name
            itemView.locality_text.text = item.restaurant.location?.locality
            itemView.rating_card_view.setBackgroundColor((Color.parseColor("#" + item.restaurant.userRating?.ratingColor)))
            itemView.rating_text_view.text = item.restaurant.userRating?.aggregateRating
            itemView.rating_text.text = item.restaurant.userRating?.ratingText

            Glide.with(itemView.context)
                .load(item.restaurant.thumb)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemView.thumbImageView)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Restaurant)
    }
}
