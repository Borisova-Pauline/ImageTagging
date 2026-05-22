package com.tomli.imagetagging

import android.app.Application
import com.tomli.imagetagging.database.ImageDB

class Applic: Application() {
    val database by lazy{ ImageDB.createDB(this) }
}