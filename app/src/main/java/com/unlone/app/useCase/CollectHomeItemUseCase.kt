package com.unlone.app.useCase

import android.util.Log
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.CommentsRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.data.TipsRepository
import com.unlone.app.model.Comment
import com.unlone.app.model.HomeUiModel
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CollectHomeItemUseCase @Inject constructor(
    private val postRepository: PostsRepository,
    private val tipsRepository: TipsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val commentRepository: CommentsRepository,
) {

    private val mComments = 1L
    suspend operator fun invoke(categories: List<String>): Flow<List<HomeUiModel?>> {
        val tips = tipsRepository.loadTips()?.documents?.map { doc ->
            HomeUiModel.Tips(
                title = doc["title"] as String,
                content = doc["content"] as String,
                action = doc["action"] as String,
                actionType = doc["action_type"] as String
            )
        }

        val parentPostItemUiStates = categories.map { ctg ->
            Log.d("TAG", "parentPostItemUiStateItems: category: $ctg")
            loadPostsFromSpecificCategory(ctg)
        }

        return combine(parentPostItemUiStates) { it2 ->
            val list: MutableList<HomeUiModel?> = it2.toMutableList()
            val iterator = list.listIterator()
            var i = 0
            var j = 0
            iterator.forEach { _ ->
                if (++i % 2 == 0 && j < tips?.size!!)
                    iterator.add(tips[j++])
            }

            Log.d("TAG", "ui item: $list")
            list.toList()
        }
    }

    private suspend fun loadPostsFromSpecificCategory(category: String): Flow<HomeUiModel.CtgPostItemUiState?> {
        val categoryKey = categoriesRepository.retrieveDefaultTopic(category)

        if (categoryKey != null) {
            Log.d("TAG", "loadPostsFromSpecificCategory: $categoryKey")
            return postRepository.getSingleCategoryPosts(categoryKey).map { postList ->

                val postListUiState = mutableListOf<PostItemUiState>()
                val iterator = postListUiState.listIterator()

                postList.forEach {
                    iterator.add(
                        PostItemUiState(
                            it.title,
                            it.imagePath,
                            it.journal.substring(0, 120.coerceAtMost(it.journal.length)),
                            it.pid,
                            getBestComment(it.pid)
                        )
                    )
                }

                val parentUiState =
                    HomeUiModel.CtgPostItemUiState(
                        category,
                        postListUiState
                    )
                parentUiState
            }
        }
        return flowOf(null)
    }

    private suspend fun getBestComment(pid: String): Comment? {
        val (commentList, _) = commentRepository.loadComments(pid, false, mComments)
        return if (commentList.isEmpty())
            null
        else
            commentList[0]
    }

}