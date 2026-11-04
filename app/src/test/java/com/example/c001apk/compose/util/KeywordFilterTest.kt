package com.example.c001apk.compose.util

import com.example.c001apk.compose.UserPreferences
import com.example.c001apk.compose.logic.datastore.UserPreferencesCompat
import com.example.c001apk.compose.logic.model.HomeFeedResponse
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class KeywordFilterTest {
    @Test fun parsesLinesWithoutEmptyRulesOrDuplicates() {
        assertEquals(listOf("【点评】", "广告"), KeywordFilter.parse(" 【点评】\r\n\n广告\n【点评】 "))
    }

    @Test fun matchesVisibleHtmlAndLiteralPunctuation() {
        assertTrue(KeywordFilter.matches(listOf("<b>【点</b>评】丝滑"), listOf("【点评】")))
        assertTrue(KeywordFilter.matches(listOf("A &amp; B"), listOf("A & B")))
        assertTrue(KeywordFilter.matches(listOf("ColorOS17"), listOf("coloros")))
        assertFalse(KeywordFilter.matches(listOf("任意内容"), emptyList()))
        assertFalse(KeywordFilter.matches(listOf("abc"), listOf("a.c")))
        assertFalse(KeywordFilter.matches(listOf("ab", "cd"), listOf("bc")))
    }

    @Test fun blocksFeedsAndForwardedContentButNotRepliesOrMessages() {
        val gson = Gson()
        fun item(type: String, content: String) = gson.fromJson(
            """{"entityType":"$type","message":"$content"}""", HomeFeedResponse.Data::class.java)
        assertTrue(KeywordFilter.blocksFeed(item("feed", "【点评】丝滑"), listOf("【点评】")))
        assertFalse(KeywordFilter.blocksFeed(item("feed_reply", "【点评】"), listOf("【点评】")))
        assertFalse(KeywordFilter.blocksFeed(item("message", "【点评】"), listOf("【点评】")))
        val forwarded = gson.fromJson(
            """{"entityType":"feed","forwardSourceFeed":{"message":"【点评】"}}""",
            HomeFeedResponse.Data::class.java)
        assertTrue(KeywordFilter.blocksFeed(forwarded, listOf("【点评】")))
    }

    @Test fun preferencesRoundTripAndOlderVersionsDefaultToEmpty() {
        assertEquals("", UserPreferencesCompat(UserPreferences.getDefaultInstance()).blockedKeywords)
        val saved = UserPreferencesCompat.default().copy(blockedKeywords = "【点评】\n广告").toProto()
        assertEquals("【点评】\n广告", UserPreferencesCompat(UserPreferences.parseFrom(saved.toByteArray())).blockedKeywords)
    }
}
