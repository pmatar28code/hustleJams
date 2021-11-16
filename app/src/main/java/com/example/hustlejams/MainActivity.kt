package com.example.hustlejams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hustlejams.databinding.ActivityMainBinding
import com.example.hustlejams.fragments.CreateWorkoutFragment
import com.example.hustlejams.fragments.PlaylistFragment
import com.example.hustlejams.fragments.WorkoutsFragment
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
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

        binding.bottomNavMain.setOnItemSelectedListener {
            handleBottomNavigation(it.itemId)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
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
                }
            }
        }
    }

    private fun connectPlaySpotify(){
        if(!alreadyConnected) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote)
            SpotifyAppRemote.connect(this, connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote
                        Repository.mSpotify = spotifyAppRemote

                        // Now you can start interacting with App Remote
                        connected()
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                })
        }else{
            mSpotifyAppRemote?.playerApi?.resume()
        }
    }

    private fun connected(){
        alreadyConnected = true
        Log.d("MainActivity", "Connected! Yay!")
        // Play a playlist
        mSpotifyAppRemote!!.playerApi.play("spotify:playlist:${Repository.newlyCratedPlaylistId}");
        mSpotifyAppRemote!!.playerApi
            .subscribeToPlayerState()
            .setEventCallback { playerState: PlayerState ->
                val track: Track? = playerState.track
                if (track != null) {
                    Log.d(
                        "MainActivity",
                        track.name.toString() + " by " + track.artist.name
                    )
                }
            }
    }
/*
    override fun onStart() {
        super.onStart()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Log.d("MainActivity", "Connected! Yay!")
                    // Play a playlist
                    mSpotifyAppRemote!!.playerApi.play("spotify:playlist:2wbN4Z9d4RipyydDbURj4t");
                    mSpotifyAppRemote!!.playerApi
                        .subscribeToPlayerState()
                        .setEventCallback { playerState: PlayerState ->
                            val track: Track? = playerState.track
                            if (track != null) {
                                Log.d(
                                    "MainActivity",
                                    track.name.toString() + " by " + track.artist.name
                                )
                            }
                        }
                    // Now you can start interacting with App Remote
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)

                    // Something went wrong when attempting to connect! Handle errors here
                }
            })
    }

    private fun connected() {

        // Then we will write some more code here.
    }

    override fun onStop() {
        super.onStop()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
        // Aaand we will finish off here.
    }

 */



    private fun swapFragments(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_main, fragment)
            .addToBackStack("back")
            .commit()
    }

    private fun handleBottomNavigation(
        menuItemId: Int
    ): Boolean = when (menuItemId) {

        R.id.menu_workouts -> {
            swapFragments(WorkoutsFragment())
            true
        }
        R.id.menu_playlists -> {

            swapFragments(PlaylistFragment())
            true
        }
        else -> false
    }

     fun test() {
        Log.e("CALLING FUN IN MAIN","TESt MAIN")
        connectPlaySpotify()
    }

    override fun onPause() {
        super.onPause()
       // mSpotifyAppRemote?.playerApi?.pause()
    }
/*
    override fun onDestroy() {
        super.onDestroy()
        mSpotifyAppRemote?.playerApi?.pause()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
    }

 */

    override fun onResume() {
        super.onResume()
        mSpotifyAppRemote?.playerApi?.resume()
    }
}