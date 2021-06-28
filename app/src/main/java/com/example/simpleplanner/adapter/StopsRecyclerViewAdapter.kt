package com.example.simpleplanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleplanner.R
import com.example.simpleplanner.models.StopLocation

class StopsRecyclerViewAdapter(private val listOfStops: List<StopLocation>):
    RecyclerView.Adapter<StopsRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int)  {
        val stop = listOfStops[position]
        holder.bind(stop)
    }

    override fun getItemCount(): Int = listOfStops.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textViewStopName = view.findViewById<TextView>(R.id.recyclerViewTextViewStopName)

        fun bind(stop: StopLocation) {
            textViewStopName?.text = stop.name

        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_recycler_view_stops, parent, false)

                return ViewHolder(view)
            }
        }
    }


}