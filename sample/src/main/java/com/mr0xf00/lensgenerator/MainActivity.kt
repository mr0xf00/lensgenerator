package com.mr0xf00.lensgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mr0xf00.lensgenerator.presentation.UsersViewModel
import com.mr0xf00.lensgenerator.ui.AppUi
import com.mr0xf00.lensgenerator.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<UsersViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state by viewModel.uiState.collectAsState(initial = null)
                    AppUi(state = state, dispatcher = viewModel::dispatchAction)
                }
            }
        }
    }
}
