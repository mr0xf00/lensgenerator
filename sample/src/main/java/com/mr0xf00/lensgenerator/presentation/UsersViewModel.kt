package com.mr0xf00.lensgenerator.presentation

import androidx.lifecycle.ViewModel
import com.mr0xf00.lensgenerator.data.*
import com.mr0xf00.lensgenerator.state.AppStateRec
import com.mr0xf00.lensgenerator.state.stateRecReducer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class UsersViewModel : ViewModel() {
    private val stateRec = MutableStateFlow(AppStateRec(AppState(emptyList())))
    val uiState: Flow<UiState> = stateRec.map { it.toUiState() }
    fun dispatchAction(action: StateAction) {
        stateRec.update { state ->
            stateRecReducer(state, action)
        }
    }
}