package com.unlone.app.useCase

import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.CommentsRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.data.TipsRepository
import com.unlone.app.model.Comment
import com.unlone.app.model.HomeUiModel
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class CollectHomeItemUseCase @Inject constructor(
    private val postRepository: PostsRepository,
    private val tipsRepository: TipsRepository,
    private val commentRepository: CommentsRepository,
    private val categoriesRepository: CategoriesRepository,
) {
    private val mComments = 1L

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<HomeUiModel>> {
        return categoriesRepository.rawCategories.filterNot { it.isEmpty() }
            .flatMapLatest { loadPostsByCategories(it) }
            .map { posts ->
                posts
            }
    }
    /*
{
    // val tipsFlow = tipsRepository.loadTips()


    val postsFlow = categoriesRepository.rawCategories.filterNot { it.isEmpty() }
        .flatMapLatest { loadPostsByCategories(it) }

    return postsFlow
    return postsFlow.combine(tipsFlow) { posts, tips ->
        Timber.d("postsPerCtgFlow: $posts")
        val list: MutableList<HomeUiModel> = posts.toMutableList()
        val iterator = list.listIterator()
        var i = 0
        var j = 0
        iterator.forEach { _ ->
            if (++i % 2 == 0 && j < tips.size)
                iterator.add(tips[j++])
        }
        Timber.d("ui item: $list")
        list.toList()
    }
}
     */

    private fun loadPostsByCategories(rawCategories: Map<String, String>) =
        combine(
            rawCategories.map { entry ->
                loadPostsFromSpecificCategory(entry).filterNotNull()
            }
        ) { it.toList() }


    private fun loadPostsFromSpecificCategory(category: Map.Entry<String, String>): Flow<HomeUiModel.CtgPostItemUiState?> =
        postRepository.getSingleCategoryPosts(category.key)
            .map { postList ->
                if (!postList.isNullOrEmpty()) {
                    val posts = HomeUiModel.CtgPostItemUiState(
                        category.value,
                        postList.map {
                            PostItemUiState(
                                it.title,
                                it.imagePath,
                                it.journal.substring(0, 120.coerceAtMost(it.journal.length)),
                                it.pid,
                                getBestComment(it.pid)
                            )
                        }
                    )
                    Timber.d(posts.toString())
                    posts
                } else null
            }


    private suspend fun getBestComment(pid: String): Comment? {
        val (commentList, _) = commentRepository.loadComments(pid, false, mComments)
        return if (commentList.isEmpty()) null else commentList[0]
    }
}
