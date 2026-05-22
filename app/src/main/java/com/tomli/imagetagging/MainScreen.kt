package com.tomli.imagetagging

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun MainScreen(){
    val section = remember { mutableStateOf(0) }
    //val searchVal = remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(drawerShape = RectangleShape/*, drawerContainerColor = Color(0xff0000AA)*/) {
                Column(modifier=Modifier.padding(horizontal = 10.dp)){
                    Text(text="Менеджер изображений", fontSize = 24.sp,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier= Modifier.height(10.dp))
                    ButtonDrawerSheet("Новая папка", {}, Icons.Default.Add)
                    ButtonDrawerSheet("Добавить изображение", {}, Icons.Default.Add)
                    ButtonDrawerSheet("Пресеты тегов", {}, Icons.Default.Star)
                    ButtonDrawerSheet("Настройки", {}, Icons.Default.Settings)

                }
            }}, drawerState = drawerState)
    {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier=Modifier.fillMaxSize().padding(bottom=innerPadding.calculateBottomPadding())) {
                Spacer(modifier=Modifier.height(innerPadding.calculateTopPadding()).fillMaxWidth()
                    .background(color=Color(0xFFC5B09B)))
                /*OutlinedTextField(value = searchVal.value, onValueChange = {n-> searchVal.value=n},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text(text="Поиск...") })
                Text(text="Это станет удобным приложением, но не сейчас")*/
                Row(modifier=Modifier.fillMaxWidth().height(55.dp).background(color= Color(0xffd6c4b2)),
                    verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier= Modifier.width(15.dp))
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null,
                        modifier = Modifier.clickable{scope.launch { drawerState.open() }})
                    Spacer(modifier= Modifier.weight(1f))
                    Text(text="Папки", color = if(section.value==0) Color(0xff55412e) else Color.Black,
                        modifier=Modifier.clickable{section.value=0 },
                        fontWeight = if(section.value==0) FontWeight.Bold else FontWeight.Normal)
                    Text(text="  |  ")
                    Text(text="Все фото", color = if(section.value==1) Color(0xff55412e) else Color.Black,
                        modifier=Modifier.clickable{section.value=1 },
                        fontWeight = if(section.value==1) FontWeight.Bold else FontWeight.Normal)
                    Spacer(modifier= Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier= Modifier.width(15.dp))
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier= Modifier.width(15.dp))
                }
                if(section.value==0){
                    Text(text="Папки")
                }else{
                    Text(text="Фото")
                }
            }
        }
    }
}


@Composable
fun ButtonDrawerSheet(text: String, onClick:()->Unit, icon: ImageVector){
    Row(modifier=Modifier.clickable{ onClick()}.padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically){
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier= Modifier.width(10.dp))
        Text(text=text)
    }
}