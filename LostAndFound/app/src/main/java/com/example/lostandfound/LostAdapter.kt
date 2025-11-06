package com.example.lostandfound

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LostAdapter(
    private var lostList: List<LostItem>,
    private val onItemClick: (LostItem) -> Unit
) :
    RecyclerView.Adapter<LostAdapter.LostViewHolder>() {

    inner class LostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lostImage: ImageView = view.findViewById(R.id.lostImage1)
        val lostName: TextView = view.findViewById(R.id.lostName1)
        val lostLocation: TextView = view.findViewById(R.id.lostLocation1)
        val lostDescription: TextView = view.findViewById(R.id.lostDescription1)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(lostList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lost, parent, false)
        return LostViewHolder(view)
    }

    override fun onBindViewHolder(holder: LostViewHolder, position: Int) {
        val item = lostList[position]

        holder.lostName.text = item.name
        holder.lostLocation.text = item.location
        holder.lostDescription.text = item.description

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.lostImage)
    }

    override fun getItemCount(): Int = lostList.size

    fun updateList(newList: List<LostItem>) {
        lostList = newList
        notifyDataSetChanged()
    }
}
