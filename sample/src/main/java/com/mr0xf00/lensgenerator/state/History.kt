package com.mr0xf00.lensgenerator.state

import com.mr0xf00.lensgenerator.data.HistoryAction
import com.mr0xf00.lensgenerator.functional.add
import com.mr0xf00.lensgenerator.functional.removeRange

const val MaxHistory = 100

data class History<S : Any> internal constructor(
    internal val rec: List<S>,
    internal val pos: Int,
    val pending: S?,
) {
    constructor(initial: S) : this(listOf(initial), 0, null)

    val canUndo: Boolean get() = pos > 0
    val canRedo: Boolean get() = pos < rec.lastIndex
    val state: S get() = pending ?: recState
}

private val <S : Any> History<S>.recState: S get() = rec[pos]

private fun <S : Any> History<S>.redo() = copy(pos = pos + 1, pending = null)

private fun <S : Any> History<S>.undo() = copy(pos = pos - 1, pending = null)

private fun <S : Any> History<S>.commit(): History<S> {
    if (pending == null || pending === recState) return this
    return copy(
        pos = pos + 1,
        rec = rec.removeRange(from = pos + 1).add(pending),
        pending = null
    ).trim()
}

private fun <S : Any> History<S>.append(newState: S): History<S> = copy(pending = newState)

private fun <S: Any> History<S>.trim() : History<S> {
    val count = rec.size - MaxHistory
    if(count < 10) return this
    return copy(pos = pos - count, rec = rec.removeRange(from = 0, toExclusive = count))
}

fun <S : Any> historyReducer(stateReducer: Reducer<S>): Reducer<History<S>> = { history, action ->
    when (action) {
        is HistoryAction -> when (action) {
            HistoryAction.Redo -> history.redo()
            HistoryAction.Undo -> history.undo()
        }
        else -> {
            val newState = stateReducer(history.state, action)
            if (newState !== history.state) history.append(newState).commit()
            else history
        }
    }
}

