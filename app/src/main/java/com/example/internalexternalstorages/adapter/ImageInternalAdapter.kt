package com.example.internalexternalstorages.adapter

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internalexternalstorages.databinding.ItemImageInternalBinding


class ImageInternalAdapter: RecyclerView.Adapter<ImageInternalAdapter.ImageViewHolder>() {


    private var imagesInternal: ArrayList<Bitmap> = ArrayList()

    inner class ImageViewHolder(val binding: ItemImageInternalBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {

            binding.image.setImageBitmap(imagesInternal[adapterPosition])

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemImageInternalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind()

    override fun getItemCount(): Int = imagesInternal.size

    fun submitDataInternal(images: ArrayList<Bitmap>) {
        this.imagesInternal = images
        notifyDataSetChanged()
    }





}