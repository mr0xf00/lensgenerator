package com.mr0xf00.lensgenerator.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mr0xf00.lensgenerator.R
import com.mr0xf00.lensgenerator.data.*
import com.mr0xf00.lensgenerator.presentation.UiState

@Composable
fun AppUi(
    state: UiState?,
    dispatcher: (StateAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (state == null) CircularProgressIndicator()
        else Users(state, dispatcher)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Users(
    state: UiState,
    dispatcher: (StateAction) -> Unit
) {
    var editState by remember { mutableStateOf<Int?>(null) }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar {
            IconButton(onClick = { dispatcher(HistoryAction.Undo) }, enabled = state.canUndo) {
                Icon(painterResource(id = R.drawable.undo), null)
            }
            IconButton(onClick = { dispatcher(HistoryAction.Redo) }, enabled = state.canRedo) {
                Icon(painterResource(id = R.drawable.redo), null)
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { dispatcher.createUser(state.users.size) }) {
            Icon(Icons.Default.Add, null)
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.users.size, key = { state.users[it].id.value }) { idx ->
                val user = state.users[idx]
                Card(
                    elevation = 4.dp, modifier = Modifier
                        .fillParentMaxWidth()
                        .height(50.dp)
                        .animateItemPlacement()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(user.name)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { editState = idx }) {
                            Icon(Icons.Default.Edit, null)
                        }
                        IconButton(onClick = { dispatcher(AppStateAction.DeleteUser(idx)) }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }
        }
    }
    editState?.let { userIndex ->
        val userName = state.users[userIndex].name
        EditNameDialog(userName, onCancel = { editState = null },
            onAccept = { newName ->
                dispatcher(AppStateAction.SetUserName(userIndex, newName))
                editState = null
            }
        )
    }
}

@Composable
fun EditNameDialog(
    initialName: String,
    onCancel: () -> Unit,
    onAccept: (String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    AlertDialog(onDismissRequest = onCancel, confirmButton = {
        Button(onClick = { onAccept(name) }) {
            Text("OK")
        }
    }, dismissButton = {
        Button(onClick = onCancel) {
            Text("CANCEL")
        }
    }, text = {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("User name") }
        )
    })
}


private fun ((StateAction) -> Unit).createUser(count: Int) {
    this(AppStateAction.AddUser(User(UserId.random(), "User ${count + 1}", UserType.Admin)))
}