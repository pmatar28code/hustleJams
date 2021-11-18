package com.example.hustlejams.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkoutDao {
    @Insert
    fun addWorkout(workout: WorkoutClass)

    @Query("SELECT * FROM WorkoutClass")
    fun getAllWorkouts(): List<WorkoutClass>

    @Query("DELETE FROM WorkoutClass WHERE `key` = :workoutKey")
    fun removeWorkout(workoutKey:Int):Int

    @Query("DELETE FROM WorkoutClass")
    fun deleteAllWorkouts()


}