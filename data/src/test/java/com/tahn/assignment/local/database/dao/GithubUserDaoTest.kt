package com.tahn.assignment.local.database.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.database.entity.GithubUserEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GithubUserDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: GithubUserDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.githubUserDao()
    }

    @Test
    fun insertUsers_and_getPagingSource_shouldReturnInsertedUsers() =
        runTest {
            val userList = mockUser()

            dao.insertUsers(userList)

            val pagingSource = dao.getUserPagingSource()
            val loadResult =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = 10,
                        placeholdersEnabled = false,
                    ),
                )

            val expected =
                PagingSource.LoadResult.Page(
                    data = userList,
                    prevKey = null,
                    nextKey = null,
                )

            assertEquals(expected.data, (loadResult as PagingSource.LoadResult.Page).data)
        }

    @Test
    fun clearAll_shouldDeleteAllUsers() =
        runTest {
            val userList = mockUser()

            dao.insertUsers(userList)
            dao.clearAll()

            val pagingSource = dao.getUserPagingSource()
            val loadResult =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = 10,
                        placeholdersEnabled = false,
                    ),
                )

            assertTrue((loadResult as PagingSource.LoadResult.Page).data.isEmpty())
        }

    @After
    fun tearDown() {
        database.close()
    }

    // Mock user list with random nullable properties
    private fun mockUser() =
        (1..5).map {
            GithubUserEntity(
                id = it,
                login = if (it % 2 == 0) null else "username$it",
                avatarUrl = if (it % 2 == 0) null else "avatar$it",
                profileUrl = if (it % 2 == 0) null else "profile$it",
            )
        }
}
