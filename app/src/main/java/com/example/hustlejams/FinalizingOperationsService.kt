package com.example.hustlejams

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class FinalizingOperationsService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FOService", "Service Started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FOService", "Service Destroyed")

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        //Log.e("FOService", "Service Ends")
        Repository.mSpotify?.playerApi?.pause()
        //SpotifyAppRemote.disconnect(Repository.mSpotify)
        stopSelf()
    }
    /*
    fun tasksToRemove(){
        Log.e("FOService", "Service Ends")
        Repository.mSpotify?.playerApi?.pause()
        SpotifyAppRemote.disconnect(Repository.mSpotify)
    }

     */
}