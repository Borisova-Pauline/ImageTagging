package com.tomli.imagetagging.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImagesData::class, TagPreset::class], version = 1,
    exportSchema = true, autoMigrations = [])
abstract class ImageDB : RoomDatabase() {
    abstract val daoData: DaoData
    companion object{
        fun createDB(context: Context): ImageDB{
            return Room.databaseBuilder(context, ImageDB::class.java, "imageTagging.db")//.fallbackToDestructiveMigration()
                .createFromAsset("imageTagging.db").build()
        }
    }
}