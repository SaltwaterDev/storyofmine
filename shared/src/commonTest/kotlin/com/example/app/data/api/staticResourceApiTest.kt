package com.example.app.data.api

import com.example.app.data.rules.Rule
import com.example.app.data.rules.RulesResponse
import com.example.app.data.write.GuidingQuestion
import com.example.app.data.write.GuidingQuestionListResponse
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*

class StaticResourcesApiTest : FunSpec({

    test("getGuidingQuestions success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"635e736b7b92802d7ad802b9","text":"Is there anything in the past that is related to the current issue?"}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val staticResourceApiService = StaticResourcesApiService(mockEngine)
        val guidingQuestionResponse = staticResourceApiService.getGuidingQuestions()

        val guidingQuestionResponseMatcher = Matcher<GuidingQuestionListResponse> { value ->
            MatcherResult(
                value == GuidingQuestionListResponse(
                    listOf(
                        GuidingQuestion(
                            "Is there anything in the past that is related to the current issue?",
                            "635e736b7b92802d7ad802b9"
                        )
                    )
                ),
                { "GuidingQuestionListResponse $value should be 635e736b7b92802d7ad802b9 Is there anything in the past that is related to the current issue?" },
                { "GuidingQuestionListResponse $value should be 635e736b7b92802d7ad802b9 Is there anything in the past that is related to the current issue?" },
            )
        }
        guidingQuestionResponse.shouldBe(guidingQuestionResponseMatcher)

    }

    test("getGuidingQuestions fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val staticResourceApiService = StaticResourcesApiService(mockEngine)
        shouldThrowAny {
            staticResourceApiService.getGuidingQuestions()
        }
    }

    test("getRules success").config(coroutineTestScope = true) {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel("""{"data":[{"id":"635e94a33515146f4749c574","text":"Share genuine stories, but you may use fictitious names or metaphors in place of events and characters."}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val staticResourceApiService = StaticResourcesApiService(mockEngine)
        val rulesResponse = staticResourceApiService.getRules()
        val rulesResponseMatcher = Matcher<RulesResponse> { value ->
            MatcherResult(
                value == RulesResponse(
                    listOf(
                        Rule(
                            "635e94a33515146f4749c574",
                            "Share genuine stories, but you may use fictitious names or metaphors in place of events and characters."
                        )
                    )
                ),
                { "RulesResponse $value should be 635e94a33515146f4749c574 Share genuine stories, but you may use fictitious names or metaphors in place of events and characters."},
                { "RulesResponse $value should be 635e94a33515146f4749c574 Share genuine stories, but you may use fictitious names or metaphors in place of events and characters."},
            )
        }
        rulesResponse.shouldBe(rulesResponseMatcher)
    }

    test("getRules fail").config(coroutineTestScope = true) {
        val mockEngine = MockEngine { respondBadRequest() }
        val staticResourceApiService = StaticResourcesApiService(mockEngine)
        shouldThrowAny {
            staticResourceApiService.getRules()
        }
    }
})