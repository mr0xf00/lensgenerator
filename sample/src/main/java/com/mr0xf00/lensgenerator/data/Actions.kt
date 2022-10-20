package com.mr0xf00.lensgenerator.data

sealed interface StateAction

sealed interface AppStateAction : StateAction {
    data class AddUser(val user: User) : AppStateAction
    data class SetUserName(val index: Int, val name: String) : AppStateAction // should be an id
    data class SetUserType(val index: Int, val type: UserType) : AppStateAction
    data class DeleteUser(val index: Int) : AppStateAction
}

sealed interface HistoryAction : StateAction {
    object Undo : HistoryAction
    object Redo : HistoryAction
}