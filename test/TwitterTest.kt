package com.ntihs_fk

import org.junit.Test
import socialSoftware.twitter.postTweet

class TwitterTest {
    private val text = "\\Young 教我/" +
            "台南高工網頁設計社電神Young" +
            "幫網頁社寫了一個官網" +
            "很會Python的專家" +
            "很會社交的社交大師" +
            "Facebook: Yang Wang" +
            "Twitter: Young___TW" +
            "Instagram: _young_wang" +
            "GitHub: Young-TW" +
            "Blog: Young Blog" +
            "Contact: young20050727@gmail.com" +
            "'信不信我用OSU電爆你'---Young 2021.08.23"

    @Test
    fun twitterTest() {
        postTweet(text, null, "a")
    }
}