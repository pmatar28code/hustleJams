package com.example.hustlejams.networking.networkClasses


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateList(
    @Json(name = "collaborative")
    val collaborative: Boolean?,
    @Json(name = "description")
    val description: String?,
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
    @Json(name = "name")
    val name: String?,
    @Json(name = "owner")
    val owner: Owner?,
    @Json(name = "primary_color")
    val primaryColor: Any?,
    @Json(name = "public")
    val `public`: Boolean?,
    @Json(name = "snapshot_id")
    val snapshotId: String?,
    @Json(name = "tracks")
    val tracks: Tracks?,
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

    @JsonClass(generateAdapter = true)
    data class Owner(
        @Json(name = "display_name")
        val displayName: String?,
        @Json(name = "external_urls")
        val externalUrls: ExternalUrls?,
        @Json(name = "href")
        val href: String?,
        @Json(name = "id")
        val id: String?,
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
    }

    @JsonClass(generateAdapter = true)
    data class Tracks(
        @Json(name = "href")
        val href: String?,
        @Json(name = "items")
        val items: List<Any?>?,
        @Json(name = "limit")
        val limit: Int?,
        @Json(name = "next")
        val next: Any?,
        @Json(name = "offset")
        val offset: Int?,
        @Json(name = "previous")
        val previous: Any?,
        @Json(name = "total")
        val total: Int?
    )
}