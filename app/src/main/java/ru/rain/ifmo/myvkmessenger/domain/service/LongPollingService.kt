package ru.rain.ifmo.myvkmessenger.domain.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.vk.api.sdk.VK
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import ru.rain.ifmo.myvkmessenger.App
import ru.rain.ifmo.myvkmessenger.data.models.VKLongPollServer
import ru.rain.ifmo.myvkmessenger.data.models.VKMessage
import ru.rain.ifmo.myvkmessenger.data.requests.VKGetLongPollServerRequest
import ru.rain.ifmo.myvkmessenger.domain.engine.VKCoreEngine
import java.util.concurrent.TimeUnit

class LongPollingService : Service() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var currentServer: VKLongPollServer

    private val client = OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .build()

    override fun onBind(intent: Intent?): IBinder? {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        if (compositeDisposable.isDisposed) {
            startLongPolling()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        VKCoreEngine.VKConversationEngine.startLongPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun repeatObservable() = Observable.create<JSONArray> { emitter ->
        while (!emitter.isDisposed) {
            Log.d("LongPollingTag", "New request")
            val url = HttpUrl.parse("https://${currentServer.server}")!!.newBuilder()
                .addQueryParameter("act", "a_check")
                .addQueryParameter("key", currentServer.key)
                .addQueryParameter("ts", currentServer.ts.toString())
                .addQueryParameter("wait", "25")
                .addQueryParameter("mode", "2")
                .addQueryParameter("version", "3")
                .build()
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val bodyString = body.string()
                val json = JSONObject(bodyString)
                when (val validation = validateFailure(json)) {
                    is SuccessfulResponse -> {
                        val ts = json.optInt("ts")
                        currentServer.ts = ts
                        emitter.onNext(json.getJSONArray("updates"))
                    }
                    is FailureNewTs -> {
                        Log.d("LongPollingTag", "New ts for failure")
                        currentServer.ts = validation.ts
                    }
                    is FailureNewServer -> {
                        emitter.onComplete()
                    }
                }
            }
        }
    }
        .subscribeOn(Schedulers.single())

    fun startLongPolling() {
        compositeDisposable.add(
            Single.create<VKLongPollServer> {
                it.onSuccess(VK.executeSync(VKGetLongPollServerRequest()))
            }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Log.d("LongPollingTag", "Got long poll server $it")
                    compositeDisposable.clear()
                    currentServer = it
                    repeatLongPoll()
                },{
                    Log.d("LongPollingTag", "Exception at startLongPolling $it")
                })
        )
    }

    private fun repeatLongPoll() {
        //TODO(Adopt this method to make updates more complicated)
        compositeDisposable.add(
            repeatObservable()
                .flatMap { it.toObservable() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Log.d("LongPollingTag", "New updates are got $it")

                }, {
                    Log.d("LongPollingTag", "Exception is handled $it")
                }, {
                    compositeDisposable.clear()
                    startLongPolling()
                })
        )

    }

    inner class MyBinder: Binder() {
        fun service() = this@LongPollingService
    }

    companion object {
        private fun validateFailure(response: JSONObject): ServerResponseValidation  {
            if (!response.has("failed"))
                return SuccessfulResponse
            return when (response.optInt("failed")) {
                1 -> FailureNewTs(response.optInt("ts"))
                2 -> FailureNewServer
                else -> throw IllegalArgumentException("Can't parse json")
            }
        }

        private fun JSONArray.toObservable() = Observable.create<VKMessage> {
            for (i in 0 until length()) {
                val currentArray = getJSONArray(i)
                if (currentArray.getInt(0) == 4) {
                    val peerId = currentArray.getInt(3)
                    if (peerId < 2000000000) {
                        val msgId = currentArray.getInt(1)
                        val date = currentArray.getInt(4)
                        val text = currentArray.getString(5)
                        val mask = currentArray.getInt(2)
                        val fromId = if (mask and 2 == 2) {
                            //Outcome message
                            App.token!!.userId
                        } else {
                            peerId
                        }
                        it.onNext(
                            VKMessage(msgId, date, peerId, fromId, text)
                        )
                    }
                }
            }
        }
    }


}