package com.intermediate.storyapp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intermediate.storyapp.R
import com.intermediate.storyapp.data.response.ListStoryItem
import com.intermediate.storyapp.view.detail.DetailActivity

class MainAdapter : PagingDataAdapter<ListStoryItem, MainAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgItemPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: ListStoryItem) {
            Glide.with(itemView)
                .load(story.photoUrl)
                .into(imgItemPhoto)

            tvItemName.text = story.name
            tvItemDescription.text = story.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("STORY_ID", story.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}