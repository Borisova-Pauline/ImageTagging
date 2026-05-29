package com.tomli.imagetagging

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.tomli.imagetagging.database.ImageVM
import com.tomli.imagetagging.database.ImagesData
import kotlinx.serialization.json.Json

@Composable
fun OneFolderScreen(navController: NavController, imageVM: ImageVM){
    val destiny = LocalDensity.current
    val cellWidth = remember{ mutableStateOf(100.dp)}
    val images = remember { mutableStateOf<List<ImagesData>>(emptyList()) }
    imageVM.GetImagesInFolder{n-> images.value=n}
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
                    modifier = Modifier.clickable {
                        navController.navigateUp()
                        imageVM.folderId.value = 0
                        imageVM.folder.value = ""
                    })
                Spacer(modifier = Modifier.weight(1f))
                Text(text = imageVM.folder.value, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
            }
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)) {
                item{
                    Box(modifier=Modifier.padding(5.dp).fillMaxSize().onGloballyPositioned { coordinates->cellWidth.value=with(destiny){coordinates.size.width.toDp()} }.height(cellWidth.value).background(color=Color(0xff191919))
                        .clickable {
                            imageVM.isImageEditing.value=false
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
                            imageVM.imageId.value = item.id!!
                            imageVM.selectedImageUri.value = item.image_uri!!.toUri()
                            imageVM.GetFolderName()
                            imageVM.tagsList = Json.decodeFromString<MutableList<String>>(item.tags!!)
                            imageVM.keyWords.value = item.keyWords!!
                            navController.navigate("viewImage")
                        }, contentAlignment = Alignment.Center){
                        AsyncImage(model=item.image_uri!!.toUri(), contentDescription = null, contentScale = ContentScale.Crop)
                    }
                }
            }
        }
    }
}