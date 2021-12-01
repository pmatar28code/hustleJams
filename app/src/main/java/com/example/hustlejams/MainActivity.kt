package com.example.hustlejams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hustlejams.databinding.ActivityMainBinding
import com.example.hustlejams.fragments.PlaylistFragment
import com.example.hustlejams.fragments.WorkoutsFragment
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class MainActivity : AppCompatActivity() {
    private val CLIENT_ID = "70280efd1f0c4d8291e7dccf08d22662"
    private val REDIRECT_URI = "hustlejams://callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var connectionParams:ConnectionParams ?= null
    private var alreadyConnected = false
    private val REQUEST_CODE = 1337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)


        connectionParams = ConnectionParams.Builder(CLIENT_ID)
          .setRedirectUri(REDIRECT_URI)
          .showAuthView(true)
           .build()

        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI
        )

        builder.setScopes(arrayOf("streaming","playlist-modify-private","playlist-read-private","user-read-private","user-read-playback-state","user-library-modify","user-read-playback-position","app-remote-control","user-read-recently-played","user-modify-playback-state","user-follow-modify","playlist-modify-public","user-read-email","user-follow-read","user-read-currently-playing","playlist-read-collaborative","user-library-read","user-top-read"))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)

        justConnectPlaySpotify()

        binding.bottomNavMain.setOnItemSelectedListener {
            handleBottomNavigation(it.itemId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Repository.token = response.accessToken
                    Log.e("TOKEN: ", response.accessToken)
                    Log.e("TOKEN expire time: ", response.expiresIn.toString())
                    swapFragments(WorkoutsFragment())
                }
                AuthorizationResponse.Type.ERROR -> {
                }
                else -> {
                    finish()
                    startActivity(getIntent())
                }
            }
        }
    }

    private fun justConnectPlaySpotify(){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Repository.mSpotify = spotifyAppRemote
                    alreadyConnected = true
                }
                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)
                }
            })
    }

    private fun connectPlaySpotify(connected: (Boolean) -> Unit){
            Log.e("CONNECTING","CONNECT PLAY SPOTIFY")
            SpotifyAppRemote.disconnect(mSpotifyAppRemote)
            SpotifyAppRemote.connect(this, connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote
                        Repository.mSpotify = spotifyAppRemote
                        while(mSpotifyAppRemote == null){

                        }
                        connected(true)
                        //connected()
                    }
                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)
                    }
                })

    }

    private fun connected(playing:(Boolean) -> Unit){
        Log.e("MainActivity CONNECTED", "Connected! Yay! START PLAYING")
        mSpotifyAppRemote!!.playerApi.play("spotify:playlist:${Repository.newlyCratedPlaylistId}").setResultCallback {
            Log.e("PLAYING CALLBACK TRUE","THIS")
            playing(true)
        }

    }

    private fun swapFragments(fragment: Fragment){
        if(Repository.lastFragment == "workoutFragment") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, fragment)
                .commit()
        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, fragment)
                .addToBackStack("back")
                .commit()
        }
    }

    private fun handleBottomNavigation(
        menuItemId: Int
    ): Boolean = when (menuItemId) {

        R.id.menu_workouts -> {
            swapFragments(WorkoutsFragment())
            Repository.lastFragment = "menu_workouts"
            true
        }
        R.id.menu_playlists -> {
            swapFragments(PlaylistFragment())
            Repository.lastFragment = "menu_playlists"
            true
        }
        else -> false
    }

     fun playCurrentWorkoutPlaylist(callBack:(Boolean) -> Unit) {
         connectPlaySpotify(){ connected ->
             if(connected == true){
                 connected(){
                     callBack(it)
                 }
             }
         }
         //callBack(true)
    }
}