package com.mr0xf00.lensgenerator

import androidx.compose.ui.unit.IntSize
import com.mr0xf00.lensgenerator.functional.editValue
import com.mr0xf00.lensgenerator.functional.setValue
import org.junit.Assert
import org.junit.Test
import com.mr0xf00.lensgenerator.functional.get

@GenerateLens
data class Entries(
    val data: List<Entry>
) {
    constructor(vararg entries: Entry) : this(listOf(*entries))
    companion object
}

class SealedTest {
    private val entries = Entries(
        UserEntry(name = "name0", photoUrl = ""),
        AudioEntry(name = "name0", lengthMS = 2000),
        ImageEntry(name = "name0", size = IntSize.Zero)
    )

    @Test
    fun entryNameIsChanged() {
        var entry = entries.data.first()
        entry = entry.setValue(Entry.name, "name1")
        Assert.assertEquals("name1", entry.name)
    }

    @Test
    fun entryNameInCollectionIsChanged() {
        var newEntries = entries
        newEntries.data.indices.forEach { i ->
//            newEntries = Entries.data[i].name.set(newEntries, "name1")
            newEntries = newEntries.editValue(Entries.data[i].name) { it + "_postfix" }
        }
        for(e in newEntries.data) {
            Assert.assertEquals("name0_postfix", e.name)
        }
    }
}