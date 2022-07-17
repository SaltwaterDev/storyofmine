package com.unlone.app.android.model


data class Story(
    var id: String = "",
    var title: String = "",
    var content: String = "",
    var topics: List<String> = emptyList(),
)


data class PostsByTopic(
    val topic: String,
    val posts: List<Story>
) {
    companion object {
        fun mock(): PostsByTopic {
            return PostsByTopic(
                topic = "topic1",
                posts = listOf(
                    Story(
                        "1",
                        "title1",
                        "content",
                        listOf("a", "b"),

                        ),
                    Story(
                        "2",
                        "title2",
                        "content",
                        listOf("a", "b"),

                        ),
                    Story(
                        "3",
                        "title4",
                        "content",
                        listOf("a", "b"),

                        ),
                    Story(
                        "4",
                        "title4",
                        "content",
                        listOf("a", "b"),

                        ),
                )
            )
        }
    }
}