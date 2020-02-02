package ru.rain.ifmo.myvkmessenger.presentation.presenter

import android.util.Log
import com.vk.api.sdk.VK
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.rain.ifmo.myvkmessenger.data.models.VKScreenConversation
import ru.rain.ifmo.myvkmessenger.data.requests.VKUsersGetRequest
import ru.rain.ifmo.myvkmessenger.domain.engine.VKCoreEngine.VKConversationEngine
import ru.rain.ifmo.myvkmessenger.domain.mvp.BasePresenter
import ru.rain.ifmo.myvkmessenger.domain.ConversationUpdateListener
import ru.rain.ifmo.myvkmessenger.presentation.view.ConversationView

class ConversationPresenter: BasePresenter<ConversationView>(),
    ConversationUpdateListener {

    private val compositeDisposable = CompositeDisposable()

    override fun onAttach() {
        Log.d("LONG", "presenter onAttach")
        VKConversationEngine.addConversationUpdateListener(this)
        getAllConversations()
    }

    private fun getAllConversations() {
        compositeDisposable.add(
            VKConversationEngine.requestAllConversations()
                .map { it.filter { conv -> conv.id > 0 } }
                .map {
                    val result = arrayListOf<VKScreenConversation>()
                    it.forEach { conv ->
                        result.add(if (conv.chatSettings == null) {
                            val user =
                                VK.executeSync(VKUsersGetRequest(intArrayOf(conv.id), arrayOf("photo_200")))[0]
                            VKScreenConversation(
                                conversation = conv,
                                photo = user.photo,
                                title = "${user.firstName} ${user.lastName}"
                            )
                        } else
                            VKScreenConversation(conv, conv.chatSettings.photo, conv.chatSettings.title)
                        )
                    }
                    result
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ screenConvList ->
                    viewState?.setConversations(screenConvList)
                }, {
                    Log.d("LONG", "OOPS, error $it")
                }, {
                    Log.d("LONG", "Completed")
                })
        )
    }

    override fun updateConversation(conversation: VKScreenConversation) {

    }

    override fun onDetach() {
        Log.d("LONG", "presenter onDetach")
        VKConversationEngine.removeConversationUpdateListener(this)
        compositeDisposable.clear()
    }
}