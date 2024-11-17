package com.erstuu.app.story.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erstuu.app.story.R
import com.erstuu.app.story.databinding.StoryItemBinding
import com.erstuu.app.story.models.Stories
import com.erstuu.app.story.ui.detail.DetailStoryActivity

class StoryAdapter : PagingDataAdapter<Stories, StoryAdapter.StoryViewHolder>(CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class StoryViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Stories) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.ivItemPhoto)

            binding.tvName.text = story.name
            binding.tvStoryDescription.text = story.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.ID, story.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object CALLBACK : DiffUtil.ItemCallback<Stories>() {
        override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
            return oldItem.name == newItem.name
        }

    }
}