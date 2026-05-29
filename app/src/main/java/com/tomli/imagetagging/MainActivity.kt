package com.tomli.imagetagging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomli.imagetagging.database.ImageVM
import com.tomli.imagetagging.ui.theme.ImageTaggingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageTaggingTheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    val imageVM: ImageVM = viewModel(factory = ImageVM.factory)
    NavHost(
        navController=navController,
        startDestination = "mainScreen"
    ) {
        composable("mainScreen"){
            MainScreen(navController, imageVM)
        }
        composable("addImage"){
            ImageAddScreen(navController, imageVM)
        }
        composable("tagsPresets"){
            TagPresetScreen(navController, imageVM)
        }
        composable("insideFolder"){
            OneFolderScreen(navController, imageVM)
        }
        composable("viewImage"){
            ViewImage(navController, imageVM)
        }
        composable("settings"){

        }
        composable("searchScreen"){

        }
    }
}


/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageTaggingTheme {
    }
}*/