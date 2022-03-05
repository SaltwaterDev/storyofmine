package com.unlone.app.model

sealed class HomeUiModel {

    data class CtgPostItemUiState(
        val category: String,
        val postsUiStateItemList: List<PostItemUiState>
    ): HomeUiModel()

    data class Tips(
        val title: String,
        val content: String,
        val action: String,
        val actionOnClick: () -> Unit,
    ): HomeUiModel()

}