package com.tomli.imagetagging.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImagesData::class, TagPreset::class, Folders::class], version = 2,
    exportSchema = true, autoMigrations = [AutoMigration(1, 2)])
abstract class ImageDB : RoomDatabase() {
    abstract val daoData: DaoData
    companion object{
        fun createDB(context: Context): ImageDB{
            return Room.databaseBuilder(context, ImageDB::class.java, "imageTagging.db")//.fallbackToDestructiveMigration()
                .createFromAsset("imageTagging.db").build()
        }
    }
}