package com.unlone.app.viewmodel

import androidx.lifecycle.*
import com.unlone.app.R
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.HomeUiModel
import com.unlone.app.useCase.CollectHomeItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    collectHomeItemUseCase: CollectHomeItemUseCase,
) : ViewModel() {

    private val followingCategories: Flow<List<String?>> = categoriesRepository.loadFollowingTopics()

    val displayingTopicsUiState =
        followingCategories.filterNotNull().map {
            Timber.d(it.toString())
            if (it.isNotEmpty()) {
                val randomFirst3Topics = it.filterNotNull().take(3)
                val topicCardList: MutableList<TopicCard> = randomFirst3Topics.map { topicTitle ->
                    TopicCard(
                        topicTitle,
                        randomColor()
                    )
                } as MutableList<TopicCard>
                topicCardList.add(
                    // special card
                    TopicCard(
                        R.string.topic_more.toString(),
                        R.drawable.gradient_ctg_more_selector
                    )
                )
                topicCardList.toList()
            } else emptyList()
        }.flowOn(Dispatchers.Default)

    val homeItemUiStateItems: StateFlow<List<HomeUiModel>> = collectHomeItemUseCase().stateIn(viewModelScope, Lazily, emptyList())


    fun searchPost(text: String) {
        // TODO ("After setting pager source")
    }

    private fun randomColor(): Int {
        val categoryCardColorList =
            listOf(
                R.drawable.gradient_ctg_selector1,
                R.drawable.gradient_ctg_selector2,
                R.drawable.gradient_ctg_selector3
            )
        return categoryCardColorList.random()
    }

    fun retrieveDefaultCategory(selectedCategory: String): Flow<String> {
        return categoriesRepository.retrieveDefaultTopic(selectedCategory)
    }

}

data class TopicCard(
    var title: String,
    val color: Int
)



