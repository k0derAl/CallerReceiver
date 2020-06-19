package com.android.callreceiver.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telecom.TelecomManager
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.android.callreceiver.R
import com.android.callreceiver.adapter.CallsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val permissions: Array<String> = arrayOf(Manifest.permission.READ_CALL_LOG)
    
    private val items: ArrayList<CallsAdapter.CallItem> = ArrayList()
    private var permissionsGranted = false
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
        setupPermissions()
        setupDefaultDialer()
    }

    private fun setupViews(){
        recycler.adapter = CallsAdapter(items)
        settings.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupPermissions(){
        permissionsGranted = checkPermissions()
        if(!permissionsGranted)
            requestPermissions(permissions, 0)
    }

    private fun setupDefaultDialer(){
        if(!checkDefaultDialer())
            startActivityForResult(Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            }, 1)
    }

    private fun checkDefaultDialer(): Boolean{
        val telecom = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        System.out.println(telecom.defaultDialerPackage)
        System.out.println(packageName)
        return (telecom.defaultDialerPackage == packageName)
    }

    private fun checkPermissions(): Boolean {
        for(permission in permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsGranted = checkPermissions()
        if(permissionsGranted)
            onResume()
        else {
            Toast.makeText(this, R.string.permissions_not_granted, LENGTH_LONG).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1)
            setupDefaultDialer()
    }

    override fun onResume() {
        super.onResume()
        if(permissionsGranted) {
            items.clear()
            items.addAll(getLastCalls())
            recycler.adapter!!.notifyDataSetChanged()
        }
    }

    private fun getLastCalls(): ArrayList<CallsAdapter.CallItem>{
        val items = ArrayList<CallsAdapter.CallItem>();

        val projection = arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE)
        val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, null)

        if(cursor != null) {
            while (cursor.moveToNext() && items.size < 100) {
                val number = cursor.getString(0)
                val type = cursor.getString(1)
                val date = cursor.getString(2)

                if(type.toInt() == CallLog.Calls.BLOCKED_TYPE){
                    items.add(CallsAdapter.CallItem(number, Date(date.toLong())))
                }
            }
            cursor.close()
        }

        items.reverse()

        return items
    }
}
