package com.unlone.app.data.api

import com.unlone.app.data.story.*
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

class StoryApiTest : FunSpec({

    test("postStory success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondOk("created-story-id") }
        val storyApiService = StoryApiService(mockEngine)
        storyApiService.postStory(
            StoryRequest(
                "title", "content", "topic",
                isPublished = true,
                commentAllowed = true,
                saveAllowed = true
            ),
            jwt = "1234"
        ).shouldBe("created-story-id")
    }

    test("postStory fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.postStory(
                StoryRequest(
                    "title", "content", "topic",
                    isPublished = true,
                    commentAllowed = true,
                    saveAllowed = true
                ),
                jwt = "1234"
            )
        }
    }

    test("fetchStoriesPerPost success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"topic":"樹洞","stories":[{"id":"639b3e9e557ffb041bfcb54a","title":"asdfasdf","content":"asdfasd","topic":"樹洞","createdDate":1671118494812}]}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.fetchStoriesPerPost(4, 4, 4)


        val storiesPerTopicsResponseMatcher = Matcher<StoriesPerTopicsResponse> { value ->
            MatcherResult(
                value == StoriesPerTopicsResponse(
                    data = listOf(
                        TopicStoryResponse(
                            "樹洞",
                            listOf(
                                SimpleStory(
                                    "639b3e9e557ffb041bfcb54a",
                                    "asdfasdf",
                                    "asdfasd",
                                    "樹洞",
                                    1671118494812
                                )
                            )
                        )
                    )
                ),
                { "StoriesPerTopicsResponse $value incorrect" },
                { "StoriesPerTopicsResponse $value incorrect" },
            )
        }

        response.shouldBe(storiesPerTopicsResponseMatcher)
    }

    test("fetchStoriesPerPost fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.fetchStoriesPerPost(4, 4, 4)
        }
    }

    test("getAllTopics success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"6367af5c098c3728e2a06fff","name":"Testing"}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.getAllTopics()
        val allTopicResponseMatcher = Matcher<AllTopicResponse> { value ->
            MatcherResult(
                value == AllTopicResponse(
                    data = listOf(Topic(id = "6367af5c098c3728e2a06fff", name = "Testing"))
                ),
                { "AllTopicResponse $value incorrect" },
                { "AllTopicResponse $value incorrect" },
            )
        }
        response.shouldBe(allTopicResponseMatcher)
    }

    test("getAllTopics fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getAllTopics()
        }
    }

    test("getRandomTopics success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"6367af5c098c3728e2a06fff","name":"Testing"}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val tokenResponse =
            storyApiService.getRandomTopics(2)

        val allTopicResponseMatcher = Matcher<AllTopicResponse> { value ->
            MatcherResult(
                value == AllTopicResponse(
                    data = listOf(Topic(id = "6367af5c098c3728e2a06fff", name = "Testing"))
                ),
                { "Token Response $value should be iq78r7qyb9" },
                { "Token Response $value should be iq78r7qyb9" },
            )
        }
        tokenResponse.shouldBe(allTopicResponseMatcher)
    }

    test("getRandomTopics fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getRandomTopics(2)
        }
    }

    test("fetchStoryDetail success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"title":"沉船好痛","content":"沉得好深","author":"629f645db3bf7a30f518f96e","topic":"感情台","isPublished":true,"commentAllowed":true,"saveAllowed":true,"createdDate":"2022-09-24T03:26:28.035Z","id":"632e78e4a425e72243b2552d","isSelfWritten":true,"isSaved":true}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.fetchStoryDetail("12345", token = "67890")

        val storyResponseMatcher = Matcher<StoryResponse> { value ->
            MatcherResult(
                value == StoryResponse(
                    "632e78e4a425e72243b2552d",
                    "沉船好痛",
                    "沉得好深",
                    "感情台",
                    "629f645db3bf7a30f518f96e",
                    true,
                    true,
                    true,
                    true,
                    true,
                    "2022-09-24T03:26:28.035Z"

                ),
                { "StoryResponse $value incorrect" },
                { "StoryResponse $value incorrect" },
            )
        }
        response.shouldBe(storyResponseMatcher)
    }

    test("fetchStoryDetail fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny { storyApiService.fetchStoryDetail("12345", token = "67890") }
    }

    test("getSameTopicStories success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"topic":"By 李怡","stories":[{"id":"635f4f6ff7638b0201a061a8","title":"1234","content":"4567jshdbdbsbs","topic":"By 李怡","createdDate":1667190639882},{"id":"636fc6a71e67ff03bfb3af42","title":"testing 3","content":"testing 3","topic":"By 李怡","createdDate":1668269735135}]}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.getSameTopicStories("12345", 4, 4)

        val storiesPerTopicsResponseMatcher = Matcher<StoriesPerTopicsResponse> { value ->
            MatcherResult(
                value == StoriesPerTopicsResponse(
                    listOf(
                        TopicStoryResponse(
                            topic = "By 李怡",
                            stories = listOf(
                                SimpleStory(
                                    "635f4f6ff7638b0201a061a8",
                                    "1234",
                                    "4567jshdbdbsbs",
                                    "By 李怡",
                                    1667190639882
                                ),
                                SimpleStory(
                                    "636fc6a71e67ff03bfb3af42",
                                    "testing 3",
                                    "testing 3",
                                    "By 李怡",
                                    1668269735135
                                )
                            )
                        )
                    )
                ),
                { "StoriesPerTopicsResponse $value incorrect" },
                { "StoriesPerTopicsResponse $value incorrect" },
            )
        }
        response.shouldBe(storiesPerTopicsResponseMatcher)
    }

    test("getSameTopicStories fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getSameTopicStories("12345", 4, 4)
        }
    }

    test("getReportReasons success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"635e792175f3c8134c21d89e","content":"Personal attack"}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.getReportReasons()
        val reportReasonResponseMatcher = Matcher<ReportReasonResponse> { value ->
            MatcherResult(
                value == ReportReasonResponse(
                    listOf(
                        ReportReasonSerializable("635e792175f3c8134c21d89e", "Personal attack")
                    )
                ),
                { "ReportReasonResponse $value incorrect" },
                { "ReportReasonResponse $value incorrect" },
            )
        }
        response.shouldBe(reportReasonResponseMatcher)
    }

    test("getReportReasons fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getReportReasons()
        }
    }

    test("postReport success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondOk()
        }
        val storyApiService = StoryApiService(mockEngine)
        storyApiService.postReport(
            ReportRequest("id", "comment", "userId"), "jwt"
        )
    }

    test("postReport fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.postReport(
                ReportRequest("id", "comment", "userId"), "jwt"
            )
        }
    }

    test("getComments success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"635e792175f3c8134c21d89e","story":"632e78e4a425e72243b2552d","text":"good","author":"wah de","createdTime":"Tue Sep 27 13:52:27 UTC 2022","isWriter":true}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response =
            storyApiService.getComments(storyId = "632e78e4a425e72243b2552d", token = "jwt")
        val commentResponseMatcher = Matcher<CommentResponse> { value ->
            MatcherResult(
                value == CommentResponse(
                    listOf(
                        CommentResponse.SingleCommentResponse(
                            "635e792175f3c8134c21d89e",
                            "632e78e4a425e72243b2552d",
                            "good",
                            "wah de",
                            "Tue Sep 27 13:52:27 UTC 2022",
                            true,
                        )
                    )
                ),
                { "CommentResponse $value incorrect" },
                { "CommentResponse $value incorrect" },
            )
        }
        response.shouldBe(commentResponseMatcher)
    }

    test("getComments fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getComments(storyId = "632e78e4a425e72243b2552d", token = "jwt")
        }
    }

    test("postComment success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"6333001b941aaa121b3a5b24","story":"632e78e4a425e72243b2552d","text":"good","author":"wah de","createdTime":"Tue Sep 27 13:52:27 UTC 2022","isWriter":true}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.postComment(CommentRequest("12345", "comment"), "jwt")
        val commentResponseMatcher = Matcher<CommentResponse> { value ->
            MatcherResult(
                value == CommentResponse(
                    listOf(
                        CommentResponse.SingleCommentResponse(
                            "6333001b941aaa121b3a5b24",
                            "632e78e4a425e72243b2552d",
                            "good",
                            "wah de",
                            "Tue Sep 27 13:52:27 UTC 2022",
                            true,
                        )
                    )
                ),
                { "CommentResponse $value incorrect" },
                { "CommentResponse $value incorrect" },
            )
        }
        response.shouldBe(commentResponseMatcher)
    }

    test("postComment fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.postComment(CommentRequest("12345", "comment"), "jwt")
        }
    }

    test("getMyStories success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"6411a29477f1cf3429040eda","title":"(Sample) duplicated: guys i'm actually AN ASSHOLE","content":"Testing \"Post","topic":"Confessing","createdDate":1678877332773}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.getMyStories("jwt")
        val storiesResponseMatcher = Matcher<StoriesResponse> { value ->
            MatcherResult(
                value == StoriesResponse(
                    listOf(
                        SimpleStory(
                            "6411a29477f1cf3429040eda",
                            "(Sample) duplicated: guys i'm actually AN ASSHOLE",
                            "Testing \"Post",
                            "Confessing",
                            1678877332773
                        )
                    )
                ),
                { "StoriesResponse $value incorrect" },
                { "StoriesResponse $value incorrect" },
            )
        }
        response.shouldBe(storiesResponseMatcher)
    }

    test("getMyStories fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getMyStories("jwt")
        }
    }

    test("getSavedStories success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"6411a29477f1cf3429040eda","title":"(Sample) duplicated: guys i'm actually AN ASSHOLE","content":"Testing \"Post","topic":"Confessing","createdDate":1678877332773}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        val response = storyApiService.getSavedStories("jwt")
        val storiesResponseMatcher = Matcher<StoriesResponse> { value ->
            MatcherResult(
                value == StoriesResponse(
                    listOf(
                        SimpleStory(
                            "6411a29477f1cf3429040eda",
                            "(Sample) duplicated: guys i'm actually AN ASSHOLE",
                            "Testing \"Post",
                            "Confessing",
                            1678877332773
                        )
                    )
                ),
                { "StoriesResponse $value incorrect" },
                { "StoriesResponse $value incorrect" },
            )
        }
        response.shouldBe(storiesResponseMatcher)
    }

    test("getSavedStories fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.getSavedStories("jwt")
        }
    }

    test("saveStory success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"6333001b941aaa121b3a5b24","story":"632e78e4a425e72243b2552d","text":"good","author":"wah de","createdTime":"Tue Sep 27 13:52:27 UTC 2022","isWriter":true}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val storyApiService = StoryApiService(mockEngine)
        storyApiService.saveStory("jwt", SaveRequest("id", true))
    }

    test("saveStory fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respondBadRequest()
        }
        val storyApiService = StoryApiService(mockEngine)
        shouldThrowAny {
            storyApiService.saveStory("jwt", SaveRequest("id", true))
        }
    }
})