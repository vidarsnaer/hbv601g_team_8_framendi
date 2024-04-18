package com.example.hbv601g_t8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.hbv601g_t8.databinding.ChatItemBinding

class ChatAdapter(private val dataset: List<Message>, private val currentUserId: Long) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, currentUserId: Long) {
            binding.messageText.text = message.message
            val layoutParams = binding.messageText.layoutParams as ConstraintLayout.LayoutParams

            if (message.senderID == currentUserId) {
                // Align to the right for the current user's messages
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                binding.messageText.setBackgroundResource(R.drawable.outgoing_message_background)
            } else {
                // Align to the left for other users' messages
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                binding.messageText.setBackgroundResource(R.drawable.incoming_message_background)
            }

            binding.messageText.layoutParams = layoutParams
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = dataset[position]
        holder.bind(message, currentUserId)
    }

    override fun getItemCount() = dataset.size

}
