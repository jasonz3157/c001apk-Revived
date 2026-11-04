package com.example.c001apk.compose.util

import com.example.c001apk.compose.logic.model.HomeFeedResponse
import org.jsoup.Jsoup

object KeywordFilter {
    fun parse(value: String): List<String> =
        value.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.distinct().toList()

    fun matches(texts: List<String?>, keywords: List<String>): Boolean {
        if (keywords.isEmpty()) return false
        return texts.filterNotNull().any { html ->
            val text = Jsoup.parseBodyFragment(html).text()
            keywords.any { it.isNotBlank() && text.contains(it, ignoreCase = true) }
        }
    }

    fun blocksFeed(item: HomeFeedResponse.Data, keywords: List<String>): Boolean =
        item.entityType == "feed" && matches(
            listOf(item.title, item.messageTitle, item.message, item.description,
                item.forwardSourceFeed?.messageTitle, item.forwardSourceFeed?.message),
            keywords,
        )
}
