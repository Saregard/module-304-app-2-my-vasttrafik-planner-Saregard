package com.example.simpleplanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplanner.R
import com.example.simpleplanner.databinding.LayoutRecyclerViewDepartureBinding
import com.example.simpleplanner.models.Departure

class DepartureRecyclerViewAdapter(
    private val listOfDepartures: List<Departure>,
    private val context: Context
) : RecyclerView.Adapter<DepartureRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfDepartures[position], context)
    }

    override fun getItemCount(): Int = listOfDepartures.size

    class ViewHolder(private val binding: LayoutRecyclerViewDepartureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("Range", "UseCompatLoadingForDrawables")
        fun bind(departure: Departure, context: Context) {
            binding.recyclerViewDepartureTextViewDirection.text = departure.direction
            binding.recyclerViewTextViewDepartureTime.text = departure.time
            binding.recyclerViewTextViewTrack.text = context.getString(R.string.platform, departure.track)
            binding.recyclerViewTextViewLineNumber.apply {
                text = departure.sname
                setTextColor(Color.parseColor(departure.bgColor))
            }
            binding.recyclerViewCardViewLineBackground.setCardBackgroundColor(
                Color.parseColor(
                    departure.fgColor
                )
            )

            val vehicleDrawable = when (departure.type) {
                "TRAM" -> R.drawable.icon_tram
                "BOAT" -> R.drawable.icon_boat
                else -> R.drawable.icon_bus
            }

            binding.recyclerViewImageViewVehicleType.setImageDrawable(
                context.getDrawable(
                    vehicleDrawable
                )
            )

        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    LayoutRecyclerViewDepartureBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}