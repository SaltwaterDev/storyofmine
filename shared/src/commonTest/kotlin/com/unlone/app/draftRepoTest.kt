package com.unlone.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


@ExperimentalCoroutinesApi
class DraftRepoTest {

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }


    @Test
    fun `getAllDraft Testing`() =
        runTest {
            // todo
           /* val draftsFlow = DraftRepositoryImpl().getAllDrafts()
            draftsFlow.collect {
                assertIs<List<ParentDraft>>(it)
            }*/
        }
}




