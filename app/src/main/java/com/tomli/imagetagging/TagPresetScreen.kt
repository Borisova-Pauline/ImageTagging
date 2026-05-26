package com.tomli.imagetagging

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.tomli.imagetagging.database.ImageVM

@Composable
fun TagPresetScreen(navController: NavController, imageVm: ImageVM){
    val tags = imageVm.allTagPresets.collectAsState(emptyList())
    val isAdding = remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(
                modifier = Modifier.height(innerPadding.calculateTopPadding()).fillMaxWidth()
                    .background(color = Color(0xFFC5B09B))
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(55.dp)
                    .background(color = Color(0xffd6c4b2)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(15.dp))
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = null,
                    modifier = Modifier.clickable { navController.navigate("mainScreen") })
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Пресеты тегов", color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        isAdding.value=true
                    })
                Spacer(modifier = Modifier.width(15.dp))
            }
            LazyColumn(modifier=Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top=10.dp)) {
                items(items = tags.value, key = {it.id!!}){ tag->
                    Row(modifier=Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Card(shape = RoundedCornerShape(5.dp), modifier = Modifier.weight(1f, fill = false)) {
                            Text(text=tag.tag!!, modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp))
                        }
                        Spacer(Modifier.width(20.dp))
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(20.dp))
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
    }
    if(isAdding.value){
        TagPresetCreator(imageVm) { isAdding.value=false }
    }
}


@Composable
fun TagPresetCreator(imageVm: ImageVM, onDismiss:()->Unit){
    val context = LocalContext.current
    val tag = remember { mutableStateOf("") }
    Dialog(onDismiss) {
        Card{
            Text(text="Добавление нового пресета тега", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top=20.dp), textAlign = TextAlign.Center)
            Spacer(modifier=Modifier.height(20.dp))
            OutlinedTextField(value = tag.value, onValueChange = {n-> tag.value=n},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                label = { Text(text="Текст тега") }, singleLine = true)
            Spacer(modifier=Modifier.height(10.dp))
            Spacer(modifier=Modifier.height(20.dp))
            Row(modifier=Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
                Text(text="Отмена", modifier = Modifier.clickable{onDismiss()})
                Spacer(modifier=Modifier.weight(1f))
                Text(text="Добавить", modifier = Modifier.clickable{
                    if(tag.value!=""){
                        imageVm.InsertTagPreset(tag.value); onDismiss()
                    }else{
                        Toast.makeText(context, "Введите текст тега", Toast.LENGTH_LONG).show()
                    }})
            }
        }
    }
}