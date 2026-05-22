package com.tomli.imagetagging.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoData {
    @Query("select * from images")
    fun GetImages(): Flow<List<ImagesData>>
}