package com.android.callreceiver.server

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.telecom.Call
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Server {
    companion object {
        private val client = OkHttpClient()

        private fun postRequest(url: String, args: HashMap<String, String> = HashMap(), callback: Callback?){
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            for(pair in args.entries)
                builder.addFormDataPart(pair.key, pair.value)
            val body = builder.build()

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).enqueue(callback?: object : Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {}
                    override fun onResponse(call: okhttp3.Call, response: Response) {}
                })
        }

        fun pushCall(phone: String, context: Context){
            val prefs = context.getSharedPreferences("data", MODE_PRIVATE)
            val serverUrl = prefs.getString("url", "")!!
            val secretKey = prefs.getString("secretKey", "")!!
            postRequest(serverUrl, hashMapOf(
                "secretKey" to secretKey,
                "phone" to phone), null)
        }
    }
}