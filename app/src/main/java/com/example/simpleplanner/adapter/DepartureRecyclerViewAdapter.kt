package com.example.simpleplanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.format.DateUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplanner.R
import com.example.simpleplanner.databinding.LayoutRecyclerViewDepartureBinding
import com.example.simpleplanner.models.Departure
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.math.abs


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
        @SuppressLint("Range", "UseCompatLoadingForDrawables", "SetTextI18n")
        fun bind(departure: Departure, context: Context) {
            val currentTime = LocalTime.now()
            val departureTime = LocalTime.parse(departure.time)
            val timeInBetweenAsMin = abs(Duration.between(currentTime, departureTime).toMinutes().toInt())
            val timeInBetweenAsHours = abs(Duration.between(currentTime, departureTime).toHours().toInt())

            if (timeInBetweenAsMin <= 59){
                binding.timeLeftTextView.text = "$timeInBetweenAsMin min"
            }else{
                binding.timeLeftTextView.text = "$timeInBetweenAsHours hours"
            }
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
                    hideInfo(context)
                }
            }
        }

        private fun hideInfo (context: Context){
            val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            animation.setAnimationListener(object: Animation.AnimationListener {

                override fun onAnimationEnd(animation: Animation?) {
                    binding.recyclerViewTextViewTrack.visibility = View.GONE
                    binding.recyclerViewTextViewDepartureTime.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationStart(animation: Animation?) {
                    TransitionManager.beginDelayedTransition(binding.departureCardView)
                }
            })
            binding.recyclerViewTextViewTrack.startAnimation(animation)
            binding.recyclerViewTextViewDepartureTime.startAnimation(animation)
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