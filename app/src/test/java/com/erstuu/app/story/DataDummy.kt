package com.erstuu.app.story

import com.erstuu.app.story.models.Stories

object DataDummy {

    fun generateDummyQuoteResponse(): List<Stories> {
        val items: MutableList<Stories> = arrayListOf()
        for (i in 0..100) {
            val quote = Stories(
                i.toString(),
                "author + $i",
                "quote $i",
            )
            items.add(quote)
        }
        return items
    }
}