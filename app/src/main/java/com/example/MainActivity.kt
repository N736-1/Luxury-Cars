package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AffiliateRepository
import com.example.ui.MainLayout
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AffiliateViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local persistence layer with Room
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AffiliateRepository(
            garageDao = database.garageDao(),
            clickDao = database.clickDao()
        )
        val viewModelFactory = AffiliateViewModel.Factory(repository)

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Instantiation of modern state-persisting Affiliate ViewModel
                    val viewModel: AffiliateViewModel = viewModel(factory = viewModelFactory)
                    
                    // Main layout rendering all catalog, garage, and dashboard graphics
                    MainLayout(viewModel = viewModel)
                }
            }
        }
    }
}
