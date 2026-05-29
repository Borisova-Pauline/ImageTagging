package com.tomli.imagetagging

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.tomli.imagetagging.database.ImageVM

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageAddScreen(navController: NavController, imageVm: ImageVM){
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri->
            imageVm.selectedImageUri.value = uri
            try{
                contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }catch (e: Exception){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    )
    val isTagAdding = remember { mutableStateOf(false) }
    val isExit = remember { mutableStateOf(false) }
    val isFolderLooking = remember { mutableStateOf(false) }
    val folders = imageVm.allFolders.collectAsState(emptyList())

    val isTagEditing = remember { mutableStateOf(false) }
    val lookingTag = remember { mutableStateOf(0) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier=Modifier.fillMaxSize().padding(bottom=innerPadding.calculateBottomPadding())) {
            Spacer(modifier=Modifier.height(innerPadding.calculateTopPadding()).fillMaxWidth()
                .background(color=Color(0xFFC5B09B)))
            Row(modifier=Modifier.fillMaxWidth().height(55.dp).background(color= Color(0xffd6c4b2)),
                verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier= Modifier.width(15.dp))
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null,
                    modifier = Modifier.clickable{ isExit.value=true })
                Spacer(modifier= Modifier.weight(1f))
                Text(text=if(imageVm.isImageEditing.value) "Редактирование изображения" else "Добавление изображения", color = Color.Black)
                Spacer(modifier= Modifier.weight(1f))
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.
                clickable{
                    if(!imageVm.isImageEditing.value){
                        if(imageVm.selectedImageUri.value!=null){
                            imageVm.InsertImages()
                            imageVm.selectedImageUri.value = null
                            //imageVm.folder.value="без папки"
                            //imageVm.folderId.value = 0
                            imageVm.tagsList = mutableListOf<String>()
                            imageVm.keyWords.value = ""
                            navController.navigateUp()
                        }
                    }else{
                        imageVm.UpdateImage()
                        imageVm.isImageEditing.value=false
                        navController.navigateUp()
                    }
                })
                Spacer(modifier= Modifier.width(15.dp))
            }
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())) {
                if(!imageVm.isImageEditing.value){
                    Button(onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }, modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                        Text(text="Выбрать изображение")
                    }
                }
                imageVm.selectedImageUri.value?.let { uri->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), verticalAlignment = Alignment.CenterVertically){
                        Text(text="Текущая папка: ${imageVm.folder.value}", modifier=Modifier.weight(1f))
                        Button(onClick = {isFolderLooking.value=true}) {
                            Text(text="Выбрать")
                        }
                    }
                    AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp))
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp), maxItemsInEachRow = Int.MAX_VALUE) {
                        Card(shape = RoundedCornerShape(5.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary),
                            modifier = Modifier.clickable{ isTagAdding.value = true}) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(5.dp))
                        }
                        imageVm.tagsList.forEachIndexed { index, tag ->
                            Card(shape = RoundedCornerShape(5.dp)) {
                                Text(text=tag, modifier = Modifier.padding(5.dp).clickable{
                                    lookingTag.value = index
                                    isTagEditing.value = true
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = imageVm.keyWords.value, onValueChange = {n-> imageVm.keyWords.value=n},
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    label = { Text(text="Ключевые слова") })
                }
            }
        }
    }
    if(isTagAdding.value){
        TagAdder(imageVm, {isTagAdding.value = false})
    }
    if(isExit.value){
        AlertDialog(
            onDismissRequest = {isExit.value=false},
            confirmButton = {Text(text="Выйти", modifier = Modifier.clickable{
                isExit.value=false
                navController.navigateUp()})},
            dismissButton = {Text(text="Остаться", modifier=Modifier.padding(end=20.dp).clickable{isExit.value=false})},
            title = {Text(text="Выйти в главное меню?")},
            text = {Text(text="Данные этого экрана сохранятся до перезапуска приложения")}
        )
    }
    if(isFolderLooking.value){
        Dialog({isFolderLooking.value=false}) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)){
                Text(text="Выбор папки", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top=20.dp), textAlign = TextAlign.Center)
                LazyColumn(modifier=Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    items(items = folders.value, key = {it.id!!}){ folder->
                        Card(shape = RoundedCornerShape(5.dp), modifier = Modifier.weight(1f, fill = false),
                            onClick = {imageVm.folder.value = folder.folderName!!
                                imageVm.folderId.value = folder.id ?: 0
                                isFolderLooking.value=false}) {
                            Text(text=folder.folderName ?: "", modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp))
                        }
                    }
                }
            }
        }
    }
    if(isTagEditing.value){
        TagEditer(imageVm, lookingTag.value) { isTagEditing.value=false }
    }
}


@Composable
fun TagAdder(imageVm: ImageVM, onDismiss:()->Unit){
    val context = LocalContext.current
    val tag = remember { mutableStateOf("") }
    val isPresetLooking = remember { mutableStateOf(false) }
    val presets = imageVm.allTagPresets.collectAsState(emptyList())
    Dialog(onDismiss) {
        Card{
            Text(text="Добавление нового тега", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top=20.dp), textAlign = TextAlign.Center)
            Spacer(modifier=Modifier.height(20.dp))
            OutlinedTextField(value = tag.value, onValueChange = {n-> tag.value=n},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                label = { Text(text="Текст тега") }, singleLine = true)
            Spacer(modifier=Modifier.height(10.dp))
            Text(text="или", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier=Modifier.height(10.dp))
            Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), onClick = {
                isPresetLooking.value=true
            }) {
                Text(text="Выбрать из пресетов")
            }
            Spacer(modifier=Modifier.height(20.dp))
            Row(modifier=Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
                Text(text="Отмена", modifier = Modifier.clickable{onDismiss()})
                Spacer(modifier=Modifier.weight(1f))
                Text(text="Добавить", modifier = Modifier.clickable{
                    if(tag.value!=""){
                        imageVm.tagsList.add(tag.value); onDismiss()
                    }else{
                        Toast.makeText(context, "Введите текст тега", Toast.LENGTH_LONG).show()
                    }})
            }
        }
    }
    if(isPresetLooking.value){
        Dialog({isPresetLooking.value=false}) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)){
                Text(text="Выбор пресета", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top=20.dp), textAlign = TextAlign.Center)
                if(presets.value.count()>0){
                    LazyColumn(modifier=Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        items(items = presets.value, key = {it.id!!}){ tag->
                            Card(shape = RoundedCornerShape(5.dp), modifier = Modifier.weight(1f, fill = false),
                                onClick = {imageVm.tagsList.add(tag.tag!!); onDismiss()}) {
                                Text(text=tag.tag!!, modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp))
                            }
                        }
                    }
                }else{
                    Text(text="Пока ещё не добавлено пресетов", modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp).fillMaxWidth(),
                    color = Color.Gray, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center)
                }
            }
        }
    }
}


@Composable
fun TagEditer(imageVm: ImageVM, tagIndex: Int, onDismiss: () -> Unit){
    val context = LocalContext.current
    val tag = remember { mutableStateOf(imageVm.tagsList[tagIndex]) }
    Dialog(onDismiss) {
        Card{
            Text(text="Редактирование тега \"${imageVm.tagsList[tagIndex]}\"", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top=20.dp), textAlign = TextAlign.Center)
            Spacer(modifier=Modifier.height(20.dp))
            OutlinedTextField(value = tag.value, onValueChange = {n-> tag.value=n},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                label = { Text(text="Текст тега") }, singleLine = true)
            Spacer(modifier=Modifier.height(30.dp))
            Row(modifier=Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
                Text(text="Отмена", modifier = Modifier.clickable{onDismiss()})
                Spacer(modifier=Modifier.weight(1f))
                Text(text="Удалить", color = Color.Red, modifier = Modifier.clickable{
                    imageVm.tagsList.removeAt(tagIndex); onDismiss()
                })
                Spacer(modifier=Modifier.weight(1f))
                Text(text="Сохранить", modifier = Modifier.clickable{
                    if(tag.value!=""){
                        imageVm.tagsList[tagIndex] = tag.value; onDismiss()
                    }else{
                        Toast.makeText(context, "Введите текст тега", Toast.LENGTH_LONG).show()
                    }})
            }
        }
    }
}