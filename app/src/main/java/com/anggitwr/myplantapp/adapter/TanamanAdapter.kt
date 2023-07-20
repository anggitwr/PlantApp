package com.anggitwr.myplantapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggitwr.myplantapp.R
import com.anggitwr.myplantapp.data.Tanaman

class TanamanAdapter(private val Tanaman: ArrayList<Tanaman>):
    RecyclerView.Adapter<TanamanAdapter.TanamanViewHoldder>() {

    private lateinit var onItemClickcallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickcallback = onItemClickCallback

    }

    interface OnItemClickCallback {
        fun onItemClicked(data:Tanaman)
    }

    class TanamanViewHoldder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val nm_tanaman = itemView.findViewById<TextView>(R.id.tv_nama_tanaman)
        val nm_latin = itemView.findViewById<TextView>(R.id.tv_nama_latin)
        val img = itemView.findViewById<ImageView>(R.id.iv_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TanamanViewHoldder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return TanamanViewHoldder(view)
    }

    override fun getItemCount(): Int = Tanaman.size

    override fun onBindViewHolder(holder: TanamanViewHoldder, position: Int) {

        val ( nm_tanaman, nm_latin, img) = Tanaman[position]

        holder.nm_tanaman.text = nm_tanaman
        holder.nm_latin.text = nm_latin
        holder.img.setImageResource(img)

        holder.itemView.setOnClickListener{ onItemClickcallback.onItemClicked(Tanaman[holder.adapterPosition]) }
    }


}