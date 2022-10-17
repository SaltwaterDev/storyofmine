package com.unlone.app.data.story

import kotlinx.serialization.Serializable


@Serializable
data class ReportReasonResponse(
    val data: List<ReportReasonSerializable>
)


@Serializable
data class ReportReasonSerializable(
    val id: String,
    val content: String,
)


fun ReportReasonSerializable.deserialize(): ReportReason = ReportReason(id, content)

data class ReportReason(
    val id: String,
    val text: String
)