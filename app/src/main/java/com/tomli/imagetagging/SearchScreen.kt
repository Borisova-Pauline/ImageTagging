package com.tomli.imagetagging

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, imageVM: ImageVM){
    val images = imageVM.allImages.collectAsState(emptyList())
    val isTagAdding = remember { mutableStateOf(false) }
    val isTagEditing = remember { mutableStateOf(false) }
    val lookingTag = remember { mutableStateOf(0) }
    val destiny = LocalDensity.current
    val cellWidth = remember{ mutableStateOf(100.dp)}
    val isDropDownExpanded = remember { mutableStateOf(false) }

    //ламбды поиска
    val allTagsSearchLambda:(image: ImagesData)-> Boolean = { image->
        val tags = Json.decodeFromString<List<String>>(image.tags!!)
        imageVM.searchTagsList.all { tag -> tags.contains(tag) }
    }
    val partiallyTagsSearchLambda:(image: ImagesData)-> Boolean = { image->
        if (imageVM.searchTagsList.isEmpty()) {
            true
        } else {
            val tags = Json.decodeFromString<List<String>>(image.tags!!)
            imageVM.searchTagsList.any { tag -> tags.contains(tag) }
        }
    }

    val filteredImages = remember(images.value) {
        derivedStateOf {
            val searchTags = imageVM.searchTagsList
            val searchKeyWords = imageVM.searchKeyWords.value
            val isFirstOption = imageVM.isFirstSearchOption.value
            val isSecondOption = imageVM.isSecondSearchOption.value

            var isFilterTags = false
            var isFilterKeyWords = false
            images.value.filter { image ->
                if (isFirstOption) {
                    isFilterTags = allTagsSearchLambda(image)
                } else {
                    isFilterTags = partiallyTagsSearchLambda(image)
                }
                isFilterKeyWords = image.keyWords!!.contains(searchKeyWords, true)

                if(isSecondOption){
                    isFilterTags && isFilterKeyWords
                }else{
                    isFilterTags || isFilterKeyWords
                }

            }
        }
    }


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
                Box{
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            isDropDownExpanded.value=true
                        })
                    DropdownMenu(expanded = isDropDownExpanded.value,
                        onDismissRequest = {isDropDownExpanded.value=false}) {
                        SearchOption("Искать по всем тегам", imageVM.isFirstSearchOption.value) {
                            imageVM.isFirstSearchOption.value=!imageVM.isFirstSearchOption.value
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
                        SearchOption("Совпадение по всем критериям", imageVM.isSecondSearchOption.value) {
                            imageVM.isSecondSearchOption.value=!imageVM.isSecondSearchOption.value
                        }
                    }
                }
                Spacer(modifier = Modifier.width(15.dp))
            }
            Text(text="По тегам:", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 20.dp))
            FlowRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).heightIn(max=100.dp)
                .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp), maxItemsInEachRow = Int.MAX_VALUE) {
                Card(shape = RoundedCornerShape(5.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.clickable{ isTagAdding.value = true}) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(5.dp))
                }
                imageVM.searchTagsList.forEachIndexed { index, tag ->
                    Card(shape = RoundedCornerShape(5.dp)) {
                        Text(text=tag, modifier = Modifier.padding(5.dp).clickable{
                            lookingTag.value = index
                            isTagEditing.value = true
                        })
                    }
                }
            }
            Spacer(Modifier.height(15.dp))
            Text(text="По ключевым словам:", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 20.dp))
            OutlinedTextField(value = imageVM.searchKeyWords.value, onValueChange = {n-> imageVM.searchKeyWords.value=n},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                label = { Text(text="Поиск") },
                leadingIcon = {Icon(imageVector = Icons.Default.Search, contentDescription = null)},
                maxLines = 5)
            Spacer(Modifier.height(15.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)) {
                items(items=filteredImages.value, key = { item-> item.id!!}){ item->
                    Box(modifier=Modifier.padding(5.dp).fillMaxSize()
                        .onGloballyPositioned { coordinates->
                            if(images.value[0]==item){
                                cellWidth.value=with(destiny){coordinates.size.width.toDp()} }
                        }
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
    if(isTagAdding.value){
        TagAdder(imageVM, true, {isTagAdding.value = false})
    }
    if(isTagEditing.value){
        TagEditer(imageVM, lookingTag.value, true) { isTagEditing.value=false }
    }
}


@Composable
fun SearchOption(text: String, option: Boolean, onClick:()->Unit){
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 5.dp)
        .width(250.dp)){
        Checkbox(checked = option, onCheckedChange = {onClick()})
        Text(text=text, modifier = Modifier.weight(1f, false))
        Spacer(Modifier.width(13.dp))
    }
}