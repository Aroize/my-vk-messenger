package ru.rain.ifmo.myvkmessenger.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_conversation.*
import ru.rain.ifmo.myvkmessenger.App
import ru.rain.ifmo.myvkmessenger.R
import ru.rain.ifmo.myvkmessenger.data.models.VKScreenConversation
import ru.rain.ifmo.myvkmessenger.domain.adapters.ConversationAdapter
import ru.rain.ifmo.myvkmessenger.presentation.presenter.ConversationPresenter
import ru.rain.ifmo.myvkmessenger.presentation.view.ConversationView

class ConversationActivity : AppCompatActivity(), ConversationView {

    private val presenter: ConversationPresenter by lazy { App.delegatePresenter(this) as ConversationPresenter }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        conversation_list.adapter = ConversationAdapter(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detach()
    }

    override fun setConversations(conversations: List<VKScreenConversation>) {
        (conversation_list.adapter as ConversationAdapter).also {
            it.conversations.beginBatchedUpdates()
            it.conversations.replaceAll(*conversations.toTypedArray())
            it.conversations.endBatchedUpdates()
        }
    }
}
