package com.example.hbv601g_t8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hbv601g_t8.databinding.ChatOverviewItemBinding

class ChatOverviewAdapter(
    private val dataset: List<Conversation>, // Use List<Conversation>
    private val onClick: (Long) -> Unit // Callback function with the conversation ID
) : RecyclerView.Adapter<ChatOverviewAdapter.ChatOverviewViewHolder>() {

    class ChatOverviewViewHolder(private val binding: ChatOverviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation, onClick: (Long) -> Unit) {
            binding.chatTitle.text = conversation.conversationTitle // Set the conversation title
            itemView.setOnClickListener { onClick(conversation.conversationId) } // Use conversation ID for the click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatOverviewViewHolder {
        val binding = ChatOverviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatOverviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatOverviewViewHolder, position: Int) {
        val conversation = dataset[position]
        holder.bind(conversation, onClick) // Bind the conversation object
    }

    override fun getItemCount() = dataset.size
}
