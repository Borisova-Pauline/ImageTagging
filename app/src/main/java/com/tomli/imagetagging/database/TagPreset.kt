package com.tomli.imagetagging.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagPreset(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val tag: String? = ""
)
