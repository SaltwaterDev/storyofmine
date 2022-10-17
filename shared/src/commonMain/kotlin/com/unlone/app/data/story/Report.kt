package com.unlone.app.data.story

import kotlinx.serialization.Serializable


@Serializable
data class ReportRequest(
    val reportReasonId: String? = null,
    val type: String,
    val reported: String,
    val otherReportReason: String? = null,
)