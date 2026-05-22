package com.tomli.imagetagging.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImagesData(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val image_uri: String? = "",
    val tags: String? = "",
    val keyWords: String? = ""
)
