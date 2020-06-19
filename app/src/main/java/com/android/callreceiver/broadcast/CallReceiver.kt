package com.android.callreceiver.broadcast


import android.telecom.Call
import android.telecom.CallScreeningService
import com.android.callreceiver.server.Server

class CallReceiver : CallScreeningService() {
    override fun onScreenCall(details: Call.Details?) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .build()
        respondToCall(details, response)
        Server.pushCall(details!!.handle.encodedSchemeSpecificPart, this)
    }
}