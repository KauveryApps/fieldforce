package com.kauveryhospital.fieldforce

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robertohuertas.endless.*

class Mains : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mains)

        title = "Endless Service"
        //  actionOnService(Actions.START)
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
        if (getServiceState(this) == ServiceState.STARTED && action == Actions.START) return
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
        val instance = Mains()
    }

}
