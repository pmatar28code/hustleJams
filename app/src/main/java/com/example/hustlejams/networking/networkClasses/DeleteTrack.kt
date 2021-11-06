package com.example.hustlejams.networking.networkClasses


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteTrack(
    @Json(name = "snapshot_id")
    val snapshotId: String?
)