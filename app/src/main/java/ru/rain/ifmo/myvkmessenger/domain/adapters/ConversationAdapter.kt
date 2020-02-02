package ru.rain.ifmo.myvkmessenger.domain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import ru.rain.ifmo.myvkmessenger.R
import ru.rain.ifmo.myvkmessenger.data.models.VKScreenConversation
import ru.rain.ifmo.myvkmessenger.domain.viewholders.ConversationViewHolder

class ConversationAdapter(private val context: Context)
    : RecyclerView.Adapter<ConversationViewHolder>() {

    private val callback = object : SortedList.Callback<VKScreenConversation>() {
        override fun areItemsTheSame(
            item1: VKScreenConversation?,
            item2: VKScreenConversation?
        ): Boolean {
            return item1?.conversation?.id == item2?.conversation?.id
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun compare(o1: VKScreenConversation?, o2: VKScreenConversation?): Int {
            o1 as VKScreenConversation; o2 as VKScreenConversation
            return o2.conversation.lastMessage!!.date - (o1.conversation.lastMessage!!.date)
        }

        override fun areContentsTheSame(
            oldItem: VKScreenConversation?,
            newItem: VKScreenConversation?
        ): Boolean {
            oldItem as VKScreenConversation; newItem as VKScreenConversation
            //TODO(Keep in mind photos)
            return (oldItem.title == newItem.title &&
                    oldItem.conversation.id == newItem.conversation.id &&
                    oldItem.conversation.inRead == newItem.conversation.inRead &&
                    oldItem.conversation.outRead == newItem.conversation.outRead &&
                    oldItem.conversation.lastMessage?.id == newItem.conversation.lastMessage?.id &&
                    oldItem.conversation.lastMessage?.text == newItem.conversation.lastMessage?.text)
        }
    }

    var conversations = SortedList<VKScreenConversation>(VKScreenConversation::class.java, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.conversation_item, parent, false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int = conversations.size()

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

}