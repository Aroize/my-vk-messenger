package ru.rain.ifmo.myvkmessenger

import android.app.Application
import android.content.Context
import android.util.Log
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAccessToken
import ru.rain.ifmo.myvkmessenger.domain.engine.VKCoreEngine
import ru.rain.ifmo.myvkmessenger.domain.mvp.BasePresenter
import ru.rain.ifmo.myvkmessenger.domain.mvp.MvpView
import ru.rain.ifmo.myvkmessenger.presentation.activity.ConversationActivity
import ru.rain.ifmo.myvkmessenger.presentation.presenter.ConversationPresenter

class App: Application() {
    companion object {

        private lateinit var appContext: Context

        var token: VKAccessToken? = null
        set(value) {
            field = value
            if (value != null) {
                VKCoreEngine.init(appContext)
            }
        }

        fun delegatePresenter(mvpView: MvpView): BasePresenter<*> {
            return when(mvpView) {
                is ConversationActivity -> {
                    ConversationPresenter()
                }
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            Log.e("APP_TAG", "VK Token is expired")
            token = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
        appContext = applicationContext
    }
}