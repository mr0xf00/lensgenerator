package com.mr0xf00.lensgenerator.state

import com.mr0xf00.lensgenerator.data.*
import com.mr0xf00.lensgenerator.functional.*

typealias Reducer<S> = (state: S, action: StateAction) -> S

private val stateReducer: Reducer<AppState> = { state, action ->
    if (action !is AppStateAction) state
    else when (action) {
        is AppStateAction.AddUser -> state.editValue(AppState.users) { it.add(action.user) }
        is AppStateAction.SetUserName -> state.setValue(
            AppState.users[action.index].name,
            action.name
        )
        is AppStateAction.SetUserType -> state.setValue(
            AppState.users[action.index].type,
            action.type
        )
        is AppStateAction.DeleteUser -> state.editValue(AppState.users) {
            it.removeAt(action.index)
        }
    }
}

private val appStateHistoryReducer = historyReducer(stateReducer)

val stateRecReducer: Reducer<AppStateRec> = { state, action ->
    state.copy(history = appStateHistoryReducer(state.history, action))
}