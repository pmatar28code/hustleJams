package com.example.hustlejams.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WorkoutClass")
data class WorkoutClass (
    @PrimaryKey(autoGenerate = true) val key:Int = 0,
    @ColumnInfo(name ="workout_name") var workout_name:String,
    @ColumnInfo(name ="workout_type")val workout_type :String,
    @ColumnInfo(name ="workout_duration")val workout_duration :String,
    @ColumnInfo(name ="playlist_json_string")val playlist_json_string:String
)

/*
fun convertTestClassToJsonString(classObj: AccessToken): String {
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<AccessToken> =
                moshi.adapter(AccessToken::class.java)
            return jsonAdapter.toJson(classObj)
        }

        fun convertJsonStringToTestClass(stringObj:String): AccessToken? {
            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<AccessToken> =
                moshi.adapter(AccessToken::class.java)
            return jsonAdapter.fromJson(stringObj)
        }
 */