package com.kauel.shippingmark.ui.fragment_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kauel.shippingmark.databinding.ItemPageBinding

class ViewPagerAdapter(private val images:List<Int>): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(private val binding: ItemPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagesRes: Int) {
            binding.ivImage.setImageResource(imagesRes)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPagerViewHolder {
        val binding = ItemPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewPagerViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }
}