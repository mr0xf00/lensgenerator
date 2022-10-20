package com.mr0xf00.lensgenerator.presentation

import com.mr0xf00.lensgenerator.data.User
import com.mr0xf00.lensgenerator.state.AppStateRec

data class UiState(
    val users: List<User>,
    val canUndo: Boolean,
    val canRedo: Boolean
)

fun AppStateRec.toUiState() : UiState = UiState(
    users = history.state.users,
    canUndo = history.canUndo,
    canRedo = history.canRedo
)