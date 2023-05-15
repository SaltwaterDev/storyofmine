package com.unlone.app.model

sealed class HomeUiState {

    data class CtgPostItemUiState(
        val category: String,
        val postsUiStateItemList: List<PostItemUiState>
    ): HomeUiState()

    data class TipsUiState(
        val title: String,
        val content: String,
        val action: String,
        val actionType: String,
    ): HomeUiState()

}