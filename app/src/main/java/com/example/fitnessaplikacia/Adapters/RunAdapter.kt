package com.example.fitnessaplikacia.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.models.Run
import com.example.fitnessaplikacia.utility.TimerUtil
import kotlinx.android.synthetic.main.run_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RunAdapter.ViewHolder>() {

    var onItemclick: ((Run) -> Unit)? = null

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.run_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val run  = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(run.img).into(ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.date
            }

            tvDistance.text = "${run.distance} km"
            tvAvgSpeed.text = "${run.avgSpeed} km/h"

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            tvTotalDistance.text = TimerUtil.getFormattedTime(run.timeInMillis)
            tvCalories.text = "${run.calories} kcal"
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(runs: List<Run>) {
        differ.submitList(runs)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}