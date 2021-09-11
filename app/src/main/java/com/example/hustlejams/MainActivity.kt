package com.example.hustlejams

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

import android.widget.TextView

import com.spotify.sdk.android.auth.AuthorizationClient

import com.spotify.sdk.android.auth.AuthorizationResponse

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View

import com.spotify.sdk.android.auth.AuthorizationRequest

import org.json.JSONException

import org.json.JSONObject

import androidx.core.content.ContextCompat
import com.example.hustlejams.databinding.ActivityMainBinding

import com.google.android.material.snackbar.Snackbar
import com.spotify.android.appremote.internal.PlayerApiImpl
import com.spotify.sdk.android.auth.BuildConfig;
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val CLIENT_ID = "70280efd1f0c4d8291e7dccf08d22662"
    private val REDIRECT_URI = "hustlejams://callback"
    val AUTH_TOKEN_REQUEST_CODE = 0x10
    val AUTH_CODE_REQUEST_CODE = 0x11

    private val mOkHttpClient: OkHttpClient = OkHttpClient()
    private var mAccessToken: String? = null
    private var mAccessCode: String? = null
    private var mCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityMainBinding.inflate(inflater)
        setContentView(R.layout.activity_main)
        supportActionBar?.setTitle(String.format(
            Locale.US, "Spotify Auth Sample %s", BuildConfig.LIB_VERSION_NAME));



    }

    override fun onDestroy() {
        cancelCall()
        super.onDestroy()
    }

    @SuppressLint("ResourceType")
    fun onGetUserProfileClicked(view: View?) {
        if (mAccessToken == null) {
            val snackbar: Snackbar = Snackbar.make(
                findViewById(R.layout.activity_main),
                R.string.warning_need_token,
                Snackbar.LENGTH_SHORT
            )
            snackbar.view.setBackgroundColor(ContextCompat.getColor(this, 1))
            snackbar.show()
            return
        }
        val request: Request = Request.Builder()
            .url ("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $mAccessToken")
            .build()
        cancelCall()
        mCall = mOkHttpClient.newCall(request)
        mCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                setResponse("Failed to fetch data: $e")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body?.string())
                    setResponse(jsonObject.toString(3))
                } catch (e: JSONException) {
                    setResponse("Failed to parse data: $e")
                }
            }
        })
    }

    fun onRequestCodeClicked(view: View?) {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.CODE)
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request)
    }

    fun onRequestTokenClicked(view: View?) {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
    }

    fun onClearCredentialsClicked(view: View?) {
        AuthorizationClient.clearCookies(this)
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
            .setShowDialog(false)
            .setScopes(arrayOf("user-read-email"))
            .setCampaign("your-campaign-token")
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.accessToken
            updateTokenView()
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.code
            updateCodeView()
        }
    }

    private fun setResponse(text: String) {
        runOnUiThread {
            val responseView = findViewById<TextView>(R.id.response_text_view)
            responseView.text = text
        }
    }

    private fun updateTokenView() {
        val tokenView = findViewById<TextView>(R.id.token_text_view)
        tokenView.text = getString(R.string.token, mAccessToken)
    }

    private fun updateCodeView() {
        val codeView = findViewById<TextView>(R.id.code_text_view)
        codeView.text = getString(R.string.code, mAccessCode)
    }

    private fun cancelCall() {
        mCall?.cancel()
    }

    private fun getRedirectUri(): Uri {
        return Uri.parse(REDIRECT_URI)
    }


}