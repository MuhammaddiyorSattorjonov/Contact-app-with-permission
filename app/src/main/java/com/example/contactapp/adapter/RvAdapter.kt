package com.example.contactapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.databinding.ItemRvBinding
import com.example.contactapp.models.Contact

class RvAdapter(val list:List<Contact>): RecyclerView.Adapter<RvAdapter.Vh>() {

    inner class Vh(val itemRvBinding:ItemRvBinding): RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(user: Contact,position: Int){
            itemRvBinding.txtName.text = user.name
            itemRvBinding.txtNumber.text = user.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position],position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}