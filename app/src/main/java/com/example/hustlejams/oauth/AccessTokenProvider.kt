package com.example.hustlejams.oauth

interface AccessTokenProvider {
    fun token(): String?

}
