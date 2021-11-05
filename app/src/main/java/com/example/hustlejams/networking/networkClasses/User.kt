package com.example.hustlejams.networking.networkClasses


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "display_name")
    val displayName: String?,
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls?,
    @Json(name = "followers")
    val followers: Followers?,
    @Json(name = "href")
    val href: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "images")
    val images: List<Any?>?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "uri")
    val uri: String?
) {
    @JsonClass(generateAdapter = true)
    data class ExternalUrls(
        @Json(name = "spotify")
        val spotify: String?
    )

    @JsonClass(generateAdapter = true)
    data class Followers(
        @Json(name = "href")
        val href: Any?,
        @Json(name = "total")
        val total: Int?
    )
}