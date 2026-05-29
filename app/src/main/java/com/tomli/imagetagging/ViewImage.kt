package com.tomli.imagetagging

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.tomli.imagetagging.database.ImageVM

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ViewImage(navController: NavController, imageVm: ImageVM){
    val context = LocalContext.current
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val isDeleting = remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black)
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(55.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = null,
                    modifier = Modifier.clickable {
                        imageVm.selectedImageUri.value = null
                        imageVm.tagsList = mutableListOf<String>()
                        imageVm.keyWords.value = ""
                        navController.navigateUp()
                    }, tint = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Share, contentDescription = null,
                    modifier = Modifier.clickable {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, imageVm.selectedImageUri.value)
                            type= "image/*"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"))
                    }, tint = Color.White)
                Spacer(modifier = Modifier.width(45.dp))
                Icon(
                    imageVector = Icons.Default.Edit, contentDescription = null,
                    modifier = Modifier.clickable {
                        imageVm.isImageEditing.value=true
                        navController.navigate("addImage")
                    }, tint = Color.White)
                Spacer(modifier = Modifier.width(30.dp))
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = null,
                    modifier = Modifier.clickable {
                        isDeleting.value = true
                    }, tint = Color.White)
                Spacer(modifier = Modifier.width(15.dp))
            }
            AsyncImage(model = imageVm.selectedImageUri.value, contentDescription = null, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Info, contentDescription = null,
                modifier = Modifier.clickable {
                    showBottomSheet.value=true
                }.fillMaxWidth().align(Alignment.CenterHorizontally).padding(vertical = 15.dp), tint = Color.White)
            if(showBottomSheet.value){
                ModalBottomSheet(onDismissRequest = {showBottomSheet.value=false}, sheetState = sheetState) {
                    Column(modifier=Modifier.padding(15.dp)) {
                        Text(text = "Папка: ${imageVm.folder.value}")
                        Spacer(Modifier.height(10.dp))
                        if(imageVm.tagsList.isNotEmpty() || imageVm.keyWords.value!=""){
                            FlowRow(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp), maxItemsInEachRow = Int.MAX_VALUE) {
                                imageVm.tagsList.forEach { tag ->
                                    Card(shape = RoundedCornerShape(5.dp)) {
                                        Text(text=tag, modifier = Modifier.padding(5.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(text = imageVm.keyWords.value)
                        }else{
                            Text(text = "Нет тегов или ключевых слов", fontStyle = FontStyle.Italic, color=Color.Gray)
                        }
                    }
                }
            }
        }
    }
    if(isDeleting.value){
        AlertDialog(
            onDismissRequest = {isDeleting.value=false},
            confirmButton = {Text(text="Удалить", modifier = Modifier.clickable{
                isDeleting.value=false
                imageVm.selectedImageUri.value = null
                imageVm.tagsList = mutableListOf<String>()
                imageVm.keyWords.value = ""
                imageVm.DeleteImage()
                navController.navigateUp()
                })},
            dismissButton = {Text(text="Отменить", modifier=Modifier.padding(end=20.dp).clickable{isDeleting.value=false})},
            title = {Text(text="Удалить изображение?")},
            text = {Text(text="Сам файл изображения на устройстве не будет удалён, удалится только запись внутри этого приложения")}
        )
    }
}