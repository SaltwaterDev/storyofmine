package com.unlone.app.android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.story.ReportReason
import com.unlone.app.data.story.ReportRepository
import com.unlone.app.data.story.StoryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val loading: Boolean = true,
    val reportSuccess: Boolean = false,
    val errorMsg: String? = null,
    val reportReasons: List<ReportReason> = listOf(),
    val otherReportReason: String = "",
    val selectedReportReason: ReportReason? = null,
    val selectedOtherReportReason: Boolean = false
)


class ReportViewModel(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<ReportUiState> =
        MutableStateFlow(ReportUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            initData()
        }
    }


    private suspend fun initData() {
        _state.value = _state.value.copy(loading = true)
        when (val result = reportRepository.getReportReasons()) {
            is StoryResult.Success -> result.data?.let {
                _state.value = state.value.copy(reportReasons = it)
            }
            is StoryResult.Failed -> {
                _state.value = state.value.copy(errorMsg = result.errorMsg)
                Log.e("TAG", "initData: ${result.errorMsg}")
            }
            is StoryResult.UnknownError -> {
                _state.value = state.value.copy(errorMsg = result.errorMsg)
            }
        }

        _state.value = _state.value.copy(loading = false)
    }

    fun sendReport(type: String, reported: String) = viewModelScope.launch {
        _state.value = state.value.copy(loading = true)
        val result = reportRepository.reportContent(
            type,
            state.value.selectedReportReason?.id,
            reported,
            state.value.selectedReportReason?.let { null } ?: state.value.otherReportReason
        )
        when (result) {
            is StoryResult.Success -> _state.value = state.value.copy(reportSuccess = true)
            is StoryResult.Failed -> _state.value = state.value.copy(errorMsg = result.errorMsg)
            is StoryResult.UnknownError -> _state.value =
                state.value.copy(errorMsg = result.errorMsg)
        }
        _state.value = state.value.copy(loading = false)
    }

    fun setOtherReportReason(content: String) {
        _state.value = state.value.copy(otherReportReason = content)
    }

    fun onReportOptionSelected(reason: ReportReason) {
        _state.value =
            state.value.copy(selectedReportReason = reason, selectedOtherReportReason = false)
    }

    fun onOtherReportSelected() {
        clearSelectedReason()
        _state.value = state.value.copy(selectedOtherReportReason = true)
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorMsg = null)
    }

    fun clearSelectedReason() {
        _state.value = _state.value.copy(selectedReportReason = null)
    }

}