package com.unlone.app.model


data class LoungePost(
    var pid: String = "",
    var title: String = "",
    var content: String = "",
    var topics: List<String> = emptyList(),
    var timeStamp: Long = 0L,
)


data class PostsByTopic(
    val topic: String,
    val posts: List<LoungePost>
) {
    companion object {
        fun mock(): PostsByTopic {
            return PostsByTopic(
                topic = "topic1",
                posts = listOf(
                    LoungePost(
                        "1",
                        "title1",
                        "content",
                        listOf("a", "b"),
                        1653225473
                    ),
                    LoungePost(
                        "2",
                        "title2",
                        "content",
                        listOf("a", "b"),
                        1653225473
                    ),
                    LoungePost(
                        "3",
                        "title4",
                        "content",
                        listOf("a", "b"),
                        1653225473
                    ),
                    LoungePost(
                        "4",
                        "title4",
                        "content",
                        listOf("a", "b"),
                        1653225473
                    ),
                )
            )
        }
    }
}