package com.robertohuertas.endless

import android.content.Intent
import android.os.Build

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kauveryhospital.fieldforce.R

class MainActivitys : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mains)
        instance = this
        title = "Endless Service"
        actionOnService(Actions.START)
//        findViewById<Button>(R.id.btnStartService).let {
//            it.setOnClickListener {
//                log("START THE FOREGROUND SERVICE ON DEMAND")
//
//
//            }
//        }

//        findViewById<Button>(R.id.btnStopService).let {
//            it.setOnClickListener {
//                log("STOP THE FOREGROUND SERVICE ON DEMAND")
//                actionOnService(Actions.STOP)
//            }
//        }

    }

    fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }
    companion object {

        lateinit var instance : MainActivitys

        fun getInstancem() : MainActivitys {

            return instance
        }
    }

}
