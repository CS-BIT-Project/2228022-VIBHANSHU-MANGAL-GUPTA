package com.example.plan_your_day

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plan_your_day.databinding.ItemForecastBinding

class ForecastAdapter : ListAdapter<ForecastItem, ForecastAdapter.ViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            binding.apply {
                dayText.text = item.day
                dateText.text = item.date
                highTemp.text = item.highTemp
                lowTemp.text = item.lowTemp

                // Set weather icon based on weather code
                weatherIcon.setImageResource(getWeatherIconResource(item.weatherCode))
            }
        }

        private fun getWeatherIconResource(weatherCode: Int): Int {
            return when (weatherCode) {
                0 -> R.drawable.ic_sunny
                1, 2, 3 -> R.drawable.ic_partly_cloudy
                45, 48 -> R.drawable.ic_foggy
                51, 53, 55, 56, 57 -> R.drawable.ic_drizzle
                61, 63, 65, 66, 67 -> R.drawable.ic_rainy
                71, 73, 75, 77 -> R.drawable.ic_snowy
                80, 81, 82 -> R.drawable.ic_rainy
                85, 86 -> R.drawable.ic_snowy
                95, 96, 99 -> R.drawable.ic_thunderstorm
                else -> R.drawable.ic_sunny
            }
        }
    }
}

class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem == newItem
    }
}