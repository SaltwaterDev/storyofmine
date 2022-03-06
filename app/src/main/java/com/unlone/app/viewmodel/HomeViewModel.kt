package com.unlone.app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.unlone.app.R
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.CommentsRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Comment
import com.unlone.app.model.HomeUiModel
import com.unlone.app.model.PostItemUiState
import com.unlone.app.useCase.CollectHomeItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeViewModel @Inject constructor(
    private val postRepository: PostsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val collectHomeItemUseCase: CollectHomeItemUseCase,
) : ViewModel() {

    private val _followingCategories: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val followingCategories: StateFlow<List<String>> = _followingCategories

    private val _categories: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val numPostsPerCategory = 5


    init {
        viewModelScope.launch {

            _followingCategories.value = categoriesRepository.loadFollowingTopics().filterNotNull()

            withContext(Dispatchers.Default) {
                _categories.value = categoriesRepository.loadCategories()
                val categoryKey =
                    _categories.value.map { categoriesRepository.retrieveDefaultTopic(it) }
                categoryKey.forEach {
                    if (it != null) {
                        postRepository.storeSingleCategoryPosts(it, numPostsPerCategory)
                    }
                }
            }
        }
    }

    val homeItemUiStateItems: StateFlow<List<HomeUiModel?>> = categories.flatMapLatest {
        collectHomeItemUseCase(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val displayingTopicsUiState =
        followingCategories.map {
            if (it.isNotEmpty()) {
                val randomFirst3Topics = it.take(3) as MutableList<String>
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
        }.asLiveData()



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

    fun retrieveDefaultCategory(selectedCategory: String): String? {
        return categoriesRepository.retrieveDefaultTopic(selectedCategory)
    }

}

data class TopicCard(
    var title: String,
    val color: Int
)



