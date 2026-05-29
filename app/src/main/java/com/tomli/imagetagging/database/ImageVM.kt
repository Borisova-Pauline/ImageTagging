package com.tomli.imagetagging.database

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.tomli.imagetagging.Applic
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ImageVM(val database: ImageDB): ViewModel() {
    val allImages = database.daoData.GetImages()
    val allFolders = database.daoData.GetFolders()
    val allTagPresets=database.daoData.GetTagPreset()


    //ImageAddScreen
    val imageId = mutableStateOf(0)
    val selectedImageUri = mutableStateOf<Uri?>(null)
    val folder = mutableStateOf("Без папки")
    val folderId = mutableStateOf(0)
    var tagsList: MutableList<String> = mutableListOf<String>()
    val keyWords = mutableStateOf("")
    val isImageEditing = mutableStateOf(false)
    fun InsertImages()=viewModelScope.launch {
        database.daoData.InsertImages(selectedImageUri.value.toString(), Json.encodeToString(tagsList), keyWords.value, folderId.value)
    }
    fun GetFolderName()=viewModelScope.launch {
        folder.value= database.daoData.GetFolderName(folderId.value)
    }
    fun DeleteImage()=viewModelScope.launch {
        database.daoData.DeleteImages(ImagesData(id=imageId.value, image_uri = selectedImageUri.value.toString(),
            tags = Json.encodeToString(tagsList), keyWords = keyWords.value, folderId.value))
    }
    fun UpdateImage()=viewModelScope.launch {
        database.daoData.UpdateImages(ImagesData(id=imageId.value, image_uri = selectedImageUri.value.toString(),
            tags = Json.encodeToString(tagsList), keyWords = keyWords.value, folderId.value))
    }


    //пресеты тегов
    fun InsertTagPreset(tag: String)=viewModelScope.launch {
        database.daoData.InsertTagPreset(tag)
    }
    fun DeleteTagPreset(tag: TagPreset)=viewModelScope.launch {
        database.daoData.DeleteTagPreset(tag)
    }
    fun UpdateTagPreset(tag: TagPreset)=viewModelScope.launch {
        database.daoData.UpdateTagPreset(tag)
    }


    //папки
    fun InsertFolder(folderName: String)=viewModelScope.launch {
        database.daoData.InsertFolder(folderName)
    }
    fun UpdateFolder(folder: Folders)=viewModelScope.launch{
        database.daoData.UpdateFolder(folder)
    }
    fun DeleteFolder(folder: Folders)=viewModelScope.launch{
        database.daoData.DeleteFolder(folder)
    }
    fun GetImagesCount(folderId: Int, onReturn:(count: Int)->Unit)=viewModelScope.launch {
        onReturn(database.daoData.GetImagesCount(folderId))
    }
    fun GetImagesInFolder(onReturn:(images: List<ImagesData>)->Unit)=viewModelScope.launch {
        onReturn(database.daoData.GetImagesInFolder(folderId.value))
    }




    companion object{
        val factory: ViewModelProvider.Factory= object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val database = (checkNotNull(extras[APPLICATION_KEY]) as Applic).database
                return ImageVM(database) as T
            }
        }
    }
}