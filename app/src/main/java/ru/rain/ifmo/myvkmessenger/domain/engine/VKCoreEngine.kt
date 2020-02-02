package ru.rain.ifmo.myvkmessenger.domain.engine

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.vk.api.sdk.VK
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.schedulers.Schedulers
import ru.rain.ifmo.myvkmessenger.data.models.VKConversation
import ru.rain.ifmo.myvkmessenger.data.requests.VKGetConversationsRequest
import ru.rain.ifmo.myvkmessenger.domain.ConversationUpdateListener
import ru.rain.ifmo.myvkmessenger.domain.service.LongPollingService

@SuppressLint("StaticFieldLeak")
object VKCoreEngine {

    //To leak because we will put there an app context
    private lateinit var context: Context

    @JvmStatic
    fun init(context: Context) {
        this.context = context.applicationContext
        VKConversationEngine.startLongPolling()
    }

    object VKConversationEngine {

        private val convUpdateList = arrayListOf<ConversationUpdateListener>()

        private lateinit var longPollingService: LongPollingService

        @JvmStatic
        fun addConversationUpdateListener(conversationUpdateListener: ConversationUpdateListener) {
            Log.d("LONG", "add conv listener")
            convUpdateList.add(conversationUpdateListener)
        }

        @JvmStatic
        fun removeConversationUpdateListener(convUpdateListener: ConversationUpdateListener) {
            convUpdateList.remove(convUpdateListener)
        }

        @JvmStatic
        fun requestAllConversations(): Observable<List<VKConversation>> =
            Observable.create { emitter: ObservableEmitter<List<VKConversation>> ->
                try {
                    val list = VK.executeSync(VKGetConversationsRequest())
                    emitter.onNext(list)
                } catch (e: Throwable) {
                    emitter.onError(e)
                } finally {
                    emitter.onComplete()
                }
            }
                .subscribeOn(Schedulers.single())

        @JvmStatic
        fun startLongPolling() {
            context.bindService(
                Intent(context, LongPollingService::class.java),
                object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        service ?: return
                        service as LongPollingService.MyBinder
                        Log.d("LongPollingTag", "Service connected name=$name")
                        longPollingService = service.service()
                        longPollingService.startLongPolling()
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        Log.d("LongPollingTag", "Service disconnected name=$name")
                    }
                },
                Context.BIND_AUTO_CREATE
            )
        }

    }
}