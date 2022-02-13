package com.unlone.app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.unlone.app.R
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.CommentsRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Comment
import com.unlone.app.model.ParentPostItemUiState
import com.unlone.app.model.PostItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeViewModel @Inject constructor(
    private val postRepository: PostsRepository,
    private val commentRepository: CommentsRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _followingCategories: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val followingCategories: StateFlow<List<String>> = _followingCategories

    private val _categories: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val numPostsPerCategory = 5
    private val mComments = 1L


    init {
        viewModelScope.launch {
            _followingCategories.value = categoriesRepository.loadFollowingTopics()
            _categories.value = categoriesRepository.loadCategories()
        }
    }

    val parentPostItemUiStateItems: StateFlow<List<ParentPostItemUiState?>> =
        categories.mapLatest { it ->
            it.map {
                loadPostsFromSpecificCategory(it)
            }
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


    private suspend fun loadPostsFromSpecificCategory(
        category: String,
        numberPost: Int = numPostsPerCategory
    ): ParentPostItemUiState? {
        val categoryKey = categoriesRepository.retrieveDefaultTopic(category)
        val posts = categoryKey?.let { postRepository.getSingleCategoryPosts(it, numberPost) }
        val postUiItemList = posts?.map {
            PostItemUiState(
                it.title,
                it.imagePath,
                it.journal.substring(0, 120.coerceAtMost(it.journal.length)),
                it.pid,
                getBestComment(it.pid)
            )
        }
        val parentUiState = postUiItemList?.let { ParentPostItemUiState(category, it) }
        Log.d("TAG", "parent ui state: $parentUiState")
        return parentUiState


    }

    private suspend fun getBestComment(pid: String): Comment? {
        val (commentList, _) = commentRepository.loadComments(pid, false, mComments)
        return if (commentList.isEmpty())
            null
        else
            commentList[0]
    }


    fun searchPost(text: String) {
        // TODO ("After setting pager source")
    }

    private fun randomColor(): Int {
        val categoryCardColorList =
            listOf(R.drawable.gradient_ctg_selector1, R.drawable.gradient_ctg_selector2, R.drawable.gradient_ctg_selector3)

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



