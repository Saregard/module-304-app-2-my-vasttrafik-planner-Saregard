package com.example.simpleplanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplanner.R
import com.example.simpleplanner.databinding.LayoutRecyclerViewDepartureBinding
import com.example.simpleplanner.models.Departure
import java.time.LocalTime

class DepartureRecyclerViewAdapter(
    private val listOfDepartures: List<Departure>,
    private val context: Context
) : RecyclerView.Adapter<DepartureRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfDepartures[position], context)
    }

    override fun getItemCount(): Int = listOfDepartures.size

    class ViewHolder(private val binding: LayoutRecyclerViewDepartureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("Range", "UseCompatLoadingForDrawables")
        fun bind(departure: Departure, context: Context) {
            val currentTime = (LocalTime.now().toSecondOfDay() / 60)
            val hour = departure.time.substring(0..1).toInt()
            val minute = departure.time.substring(3..4).toInt()
            val time = (hour * 60) + minute

            val timeLeft = time - currentTime

            binding.timeLeftTextView.text = "$timeLeft min"
            binding.leftFrame.setBackgroundColor(Color.parseColor(departure.bgColor))
            binding.recyclerViewDepartureTextViewDirection.text = departure.direction
            binding.recyclerViewTextViewDepartureTime.text = departure.time
            binding.recyclerViewTextViewTrack.text =
                context.getString(R.string.platform, departure.track)
            binding.recyclerViewTextViewLineNumber.apply {
                text = departure.sname
                setTextColor(Color.parseColor(departure.fgColor))
            }
            binding.recyclerViewCardViewLineBackground.setCardBackgroundColor(
                Color.parseColor(
                    departure.bgColor
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

            binding.departureCardView.setOnClickListener {
                if (binding.recyclerViewTextViewTrack.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(binding.departureCardView)
                    binding.recyclerViewTextViewTrack.visibility = View.VISIBLE
                    binding.recyclerViewTextViewDepartureTime.visibility = View.VISIBLE
                } else {
                    TransitionManager.beginDelayedTransition(binding.departureCardView)
                    binding.recyclerViewTextViewTrack.visibility = View.GONE
                    binding.recyclerViewTextViewDepartureTime.visibility = View.GONE
                }
            }
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