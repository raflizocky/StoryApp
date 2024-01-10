package com.intermediate.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.intermediate.storyapp.data.api.ApiService
import com.intermediate.storyapp.data.response.ListStoryItem

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val nextPage = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories(page = nextPage, size = params.loadSize)
            val stories = response.listStory
            LoadResult.Page(
                data = stories,
                prevKey = if (nextPage == INITIAL_PAGE_INDEX) null else nextPage - 1,
                nextKey = if (stories.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}