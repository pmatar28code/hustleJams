package com.example.hustlejams.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities=[WorkoutClass::class],
    version = 1
)
abstract class WorkoutDatabase: RoomDatabase(){
    abstract fun workoutDao(): WorkoutDao


    companion object {
        private const val DATABASE_NAME = "WORKOUT DATABASE"
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getInstance(context: Context):WorkoutDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}