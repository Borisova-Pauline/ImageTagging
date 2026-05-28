package com.tomli.imagetagging.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoData {
    @Query("select * from images")
    fun GetImages(): Flow<List<ImagesData>>

    @Query("select * from images where folderId=:folderId")
    suspend fun GetImagesInFolder(folderId: Int): List<ImagesData>

    @Query("insert into images(image_uri, tags, keyWords, folderId) values (:image_uri, :tags, :keyWords, :folderId)")
    suspend fun InsertImages(image_uri: String, tags: String, keyWords: String, folderId: Int)

    @Update
    suspend fun UpdateImages(image: ImagesData)

    @Delete
    suspend fun DeleteImages(image: ImagesData)


    @Query("select * from Folders")
    fun GetFolders(): Flow<List<Folders>>

    @Query("insert into Folders(folderName) values(:folderName)")
    suspend fun InsertFolder(folderName: String)

    @Update
    suspend fun UpdateFolder(folder: Folders)

    @Delete
    suspend fun DeleteFolder(folder: Folders)

    @Query("select count(*) from images where folderId=:folderId")
    suspend fun GetImagesCount(folderId: Int): Int


    @Query("select * from TagPreset")
    fun GetTagPreset(): Flow<List<TagPreset>>

    @Query("insert into TagPreset(tag) values(:tag)")
    suspend fun InsertTagPreset(tag: String)

    @Update
    suspend fun UpdateTagPreset(tag: TagPreset)

    @Delete
    suspend fun DeleteTagPreset(tag: TagPreset)
}