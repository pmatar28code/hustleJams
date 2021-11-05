package com.example.hustlejams.oauth


import android.util.Log
import com.example.hustlejams.Repository

class AccessTokenProviderImp : AccessTokenProvider {
    var token: String? = null

    override fun token(): String? {
        token = Repository.token
        Log.e("RETURNED TOKEN FUN TOKEN IMP:", "$token")
        return token
    }
}

