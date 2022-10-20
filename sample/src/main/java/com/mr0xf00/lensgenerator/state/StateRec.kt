package com.mr0xf00.lensgenerator.state

import com.mr0xf00.lensgenerator.data.AppState

data class AppStateRec(
    val history : History<AppState>
) {
    constructor(initial: AppState) : this(History(initial))
}