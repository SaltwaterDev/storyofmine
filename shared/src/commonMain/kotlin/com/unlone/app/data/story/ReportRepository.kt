package com.unlone.app.data.story

import co.touchlab.kermit.Logger
import com.unlone.app.data.auth.AuthRepository
import io.ktor.client.call.*
import io.ktor.client.plugins.*

interface ReportRepository {
    suspend fun getReportReasons(): StoryResult<List<ReportReason>>
    suspend fun reportContent(
        type: String,
        reportReasonId: String? = null,
        reported: String,
        otherReportReason: String? = null
    ): StoryResult<Unit>
}

internal class ReportRepositoryImpl(
    private val storyApi: StoryApi,
    private val authRepository: AuthRepository
) : ReportRepository {
    override suspend fun getReportReasons(): StoryResult<List<ReportReason>> {
        return try {
            val response = storyApi.getReportReasons()
            StoryResult.Success(response.data.map { it.deserialize() })
        } catch (e: RedirectResponseException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.UnknownError(errorMsg = e.message)
        }
    }

    override suspend fun reportContent(
        type: String,
        reportReasonId: String?,
        reported: String,
        otherReportReason: String?
    ): StoryResult<Unit> {
        return try {
            if (reportReasonId.isNullOrEmpty().xor(otherReportReason.isNullOrEmpty())) {
                val jwt = authRepository.getJwt()
                storyApi.postReport(
                    ReportRequest(
                        reportReasonId,
                        type,
                        reported,
                        otherReportReason
                    ),
                    token = "Bearer $jwt",
                )
                StoryResult.Success()
            } else {
                StoryResult.Failed(errorMsg = "either existing Report Reason or Other reason")
            }
        } catch (e: RedirectResponseException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: ClientRequestException) {
            StoryResult.Failed(errorMsg = e.response.body<String>())
        } catch (e: Exception) {
            Logger.e { e.toString() }
            StoryResult.Failed(errorMsg = e.message)
        }
    }

}