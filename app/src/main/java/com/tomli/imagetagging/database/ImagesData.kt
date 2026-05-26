package com.tomli.imagetagging.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "images", foreignKeys = [ForeignKey(entity = Folders::class, parentColumns = ["id"],
    childColumns = ["folderId"], onDelete = ForeignKey.SET_DEFAULT)])
data class ImagesData(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val image_uri: String? = "",
    val tags: String? = "",
    val keyWords: String? = "",
    val folderId: Int = 0
)
