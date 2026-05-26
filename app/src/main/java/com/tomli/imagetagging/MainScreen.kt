package com.tomli.imagetagging

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.tomli.imagetagging.database.ImageVM
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController, imageVM: ImageVM){
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val images = imageVM.allImages.collectAsState(initial = emptyList())
    val destiny = LocalDensity.current
    val cellWidth = remember{ mutableStateOf(100.dp)}

    val pagerState = rememberPagerState(pageCount = { 2})
    val titles = listOf("Папки", "Все фото")
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(drawerShape = RectangleShape/*, drawerContainerColor = Color(0xff0000AA)*/) {
                Column(modifier=Modifier.padding(horizontal = 10.dp)){
                    Text(text="Менеджер изображений", fontSize = 24.sp,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier= Modifier.height(10.dp))
                    ButtonDrawerSheet("Новая папка", {}, Icons.Default.Add)
                    ButtonDrawerSheet("Добавить изображение", {navController.navigate("addImage")}, Icons.Default.Add)
                    ButtonDrawerSheet("Пресеты тегов", {navController.navigate("tagsPresets")}, Icons.Default.Star)
                    ButtonDrawerSheet("Настройки", {}, Icons.Default.Settings)

                }
            }}, drawerState = drawerState)
    {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier=Modifier.fillMaxSize().padding(bottom=innerPadding.calculateBottomPadding())) {
                Spacer(modifier=Modifier.height(innerPadding.calculateTopPadding()).fillMaxWidth()
                    .background(color=Color(0xFFC5B09B)))

                Row(modifier=Modifier.fillMaxWidth().height(55.dp).background(color= Color(0xffd6c4b2)),
                    verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier= Modifier.width(15.dp))
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null,
                        modifier = Modifier.clickable{scope.launch { drawerState.open() }})
                    Spacer(modifier= Modifier.width(15.dp))
                    TabRow(selectedTabIndex = pagerState.currentPage, modifier = Modifier.weight(1f),
                        containerColor = Color(0xffd6c4b2),
                        divider = {},
                        indicator = {tabPosition->
                            if(pagerState.currentPage < tabPosition.size){
                                TabRowDefaults.Indicator(
                                    modifier=Modifier.tabIndicatorOffset(tabPosition[pagerState.currentPage]),
                                    height = 3.dp,
                                    color = Color(0xff934751)
                                )
                            }
                        }) {
                        titles.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {Text(text=title, color = if(pagerState.currentPage == index) Color(0xff934751)
                                else Color.Black)}
                            )
                        }
                    }
                    Spacer(modifier= Modifier.width(15.dp))
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier= Modifier.width(15.dp))
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier= Modifier.width(15.dp))
                }
                HorizontalPager(state = pagerState,
                    modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Top) { page ->
                    when(page){
                        0 -> {
                            Text(text="Папки", modifier = Modifier.padding(20.dp))
                        }
                        1 -> {
                            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)) {
                                item{
                                    Box(modifier=Modifier.padding(5.dp).fillMaxSize().onGloballyPositioned { coordinates->cellWidth.value=with(destiny){coordinates.size.width.toDp()} }.height(cellWidth.value).background(color=Color(0xff191919))
                                        .clickable {
                                            navController.navigate("addImage")
                                        }, contentAlignment = Alignment.Center){
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White,
                                            modifier = Modifier.size(cellWidth.value-40.dp))
                                    }
                                }
                                items(items=images.value, key = { item-> item.id!!}){ item->
                                    Box(modifier=Modifier.padding(5.dp).fillMaxSize()
                                        .background(color=Color(0xFF000000)).height(cellWidth.value)
                                        .clickable{

                                        }, contentAlignment = Alignment.Center){
                                        AsyncImage(model=item.image_uri!!.toUri(), contentDescription = null, contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                        }
                    }
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