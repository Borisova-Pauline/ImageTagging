package com.tomli.imagetagging

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tomli.imagetagging.database.Folders
import com.tomli.imagetagging.database.ImageVM

@Composable
fun FolderAddDialog(imageVm: ImageVM, isEditing: Boolean = false, folder: Folders = Folders(), onDismiss:()->Unit){
    val folderName = remember { mutableStateOf(folder.folderName!!) }
    val context = LocalContext.current
    val isDelete = remember { mutableStateOf(false) }
    Dialog(onDismiss) {
        Card{
            Text(text=if(!isEditing) "Создание новой папки" else "Редактирование папки \"${folder.folderName!!}\"", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top=20.dp), textAlign = TextAlign.Center)
            Spacer(modifier=Modifier.height(20.dp))
            OutlinedTextField(value = folderName.value, onValueChange = {n-> folderName.value=n},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                label = { Text(text="Имя папки") }, singleLine = true)
            Spacer(modifier=Modifier.height(30.dp))
            Row(modifier=Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
                Text(text="Отмена", modifier = Modifier.clickable{onDismiss()})
                Spacer(modifier=Modifier.weight(1f))
                if(isEditing){
                    Text(text="Удалить", color = Color.Red, modifier = Modifier.clickable{
                        isDelete.value=true
                    })
                    Spacer(modifier=Modifier.weight(1f))
                }
                Text(text=if(!isEditing) "Создать" else "Сохранить", modifier = Modifier.clickable{
                    if(folderName.value!=""){
                        if(isEditing){
                            imageVm.UpdateFolder(folder.copy(folderName = folderName.value))
                        }else{
                            imageVm.InsertFolder(folderName.value)
                        }
                        onDismiss()
                    }else{
                        Toast.makeText(context, "Введите название", Toast.LENGTH_LONG).show()
                    }})
            }
        }
    }
    if(isDelete.value){
        AlertDialog(
            onDismissRequest = {isDelete.value=false},
            confirmButton = {Text(text="Удалить", modifier = Modifier.clickable{
                isDelete.value=false
                imageVm.DeleteFolder(folder)
                onDismiss()})},
            dismissButton = {Text(text="Отмена", modifier=Modifier.padding(end=20.dp).clickable{isDelete.value=false})},
            title = {Text(text="Удалить папку?")},
            text = {Text(text="Все изображения внутри неё будут сохранены и перемещены в \"Без папки\"")}
        )
    }
}